package com.um.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.um.classify.CWRelationMapping;
import com.um.model.EHealthRecord;

/**
 * 根据诊断进行统计
 * @author heermaster
 *
 */
public class StatisticByDiagnose {

	
	
	public static void staitstic() throws IOException{
		//1.全部病例
		CWRelationMapping cwRelationMapping = new CWRelationMapping();
		List<EHealthRecord> eHealthRecords = cwRelationMapping.queryEhealthData();
		int length = eHealthRecords.size();
		
		File statText = new File("/Users/heermaster/Documents/file/statisbydiagnose.csv");
        FileOutputStream is = new FileOutputStream(statText);
        OutputStreamWriter osw = new OutputStreamWriter(is);    
        Writer w = new BufferedWriter(osw);
        
        // 2 key words
        Map<String, String[]> keMap = new HashMap<String, String[]>();
        String[] diagnoseStrings = diagnosekeywords;
        for(String s : diagnoseStrings){
        	String[] keStrings = s.split("%");
        	if(keStrings == null || keStrings.length != 2){
        		continue;
        	}
        	String[] keywords = keStrings[1].split("\\|");
        	keMap.put(keStrings[0], keywords);
        }
        
        DecimalFormat df = new DecimalFormat("0.00");
        // 3.statistic
        Set<String> keSet = keMap.keySet();
        for(String key : keSet){
        	String[] strings = keMap.get(key);
        	List<EHealthRecord> eRecords = statisticByDiagnoseList(eHealthRecords, strings);
        	int count = eRecords.size();
        	double percent = 100.0 * count / length;
        	w.write(key + "," + count + "," + df.format(percent)  + "%\n");
        }
        w.close();
        
	}
	
	
	public static List<EHealthRecord> statisticByDiagnoseList(List<EHealthRecord> eHealthRecords, String[] diags){
		if(eHealthRecords == null || eHealthRecords.size() == 0 || diags == null || diags.length == 0){
			return null;
		}
		List<EHealthRecord> eRecords = new ArrayList<EHealthRecord>();
		for(EHealthRecord e : eHealthRecords){
			String diagString = e.getChinesediagnostics(); // 诊断
			if(check(diagString, diags)){
				eRecords.add(e);
			}
		}
		return eRecords;
	}
	
	public static boolean check(String string,String[] strings){
		for(String s : strings){
			if(!string.matches(".*" + s + ".*")){
				return false;
			}
		}
		return true;
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		staitstic();
		System.out.println("succed!");
	}
	
	public static String[] diagnosekeywords = {
		"气虚 AND 痰瘀%气虚|痰瘀",
		"痰瘀 AND 互结%痰瘀|互结",
		"气虚 AND 互结%气虚|互结",
		"气虚 AND 痰瘀 AND 互结%气虚|痰瘀|互结",
		"痰瘀 AND 阻络%痰瘀|阻络",
		"气虚 AND 阻络%气虚|阻络",
		"气阴两虚 AND 痰瘀%气阴两虚|痰瘀",
		"瘀热 AND 气虚%瘀热|气虚",
		"瘀热 AND 痰瘀%瘀热|痰瘀",
		"气虚 AND 湿瘀%气虚|湿瘀",
		"气虚 AND 痰瘀 AND 互结 AND 阻络%气虚|痰瘀|互结|阻络",
		"互结 AND 阻络%互结|阻络",
		"互结 AND 热结%互结|热结",
		"痰瘀 AND 湿瘀%痰瘀|湿瘀",
		"热结 AND 气虚%热结|气虚",
		"脾虚 AND 气虚%脾虚|气虚",
		"脾虚 AND 痰瘀%脾虚|痰瘀",
		"痰湿 AND 痰瘀%痰湿|痰瘀",
		"脾虚 AND 互结%脾虚|互结",
		"脾虚 AND 阻络%脾虚|阻络"
	};
}
