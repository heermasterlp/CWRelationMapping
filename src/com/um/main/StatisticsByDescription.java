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
import com.um.data.DiagClassifyData;
import com.um.model.EHealthRecord;
import com.um.util.DiagMedicineProcess;

public class StatisticsByDescription {

	/**
	 * 根据描述，统计处方
	 * @param args
	 * @throws IOException 
	 */
	
	public static void statistics() throws IOException{
		
		//1.病例数据
		CWRelationMapping cwRelationMapping = new CWRelationMapping();
		List<EHealthRecord> eHealthRecords = cwRelationMapping.queryEhealthData();
		int length = eHealthRecords.size();//
		DecimalFormat df = new DecimalFormat("0.00");
		
		File statText = new File("/Users/heermaster/Documents/file/test.csv");
        FileOutputStream is = new FileOutputStream(statText);
        OutputStreamWriter osw = new OutputStreamWriter(is);    
        Writer w = new BufferedWriter(osw);
		
		//2. 关键字
		String[] keywords = DiagClassifyData.statisticsByDescription;
		
		Map<String, ArrayList<String>> keMap = new HashMap<String, ArrayList<String>>();
		
		for(String s : keywords){
			String[] keStrings = s.split("%"); // 0 :mingcheng 1:guanjianzi
			if(keStrings == null || keStrings.length != 2){
				continue;
			}
			String[] keys = keStrings[1].split("\\|");
			if(keys == null || keys.length == 0){
				continue;
			}
			ArrayList<String> list = new ArrayList<String>(keys.length);
			for(String ss : keys){
				list.add(ss);
			}
			keMap.put(keStrings[0], list);
		}
		
		System.out.println(keMap);
		
		//3.统计 输出： 名称 数量 百分比 前30味中药
		
		Set<String> keySet = keMap.keySet();
		for(String key : keySet){
			System.out.println(key);
			ArrayList<String> keywordsList = keMap.get(key);
			List<EHealthRecord> eRecords = statisticByDesc(eHealthRecords, keywordsList); //病例统计
			Map<String, Integer> medicines = DiagMedicineProcess.statisEhealthMedicine(eRecords);
			medicines = DiagMedicineProcess.sortMapByValue(medicines);
			int index = 0;
			w.write(key + ","); //mingcheng
			int count = eRecords.size();
			double percent = 100.0 * count / length;
			w.write(count + "," + df.format(percent) + "%" + ",");
			
			
			Set<String> medicineSet = medicines.keySet();
			for(String s: medicineSet){
				if(index >= 30){
					break;
				}
				int medicinecount = (int) medicines.get(s);
				w.write(s + " " + medicinecount + " (" + df.format(100.0 * medicinecount / count) + "%)" + ",");
				index++;
			}
			w.write("\n");
			System.out.println(key + "write successed!");
			
		}
		w.close();
		System.out.println("successed");
		
	}
	
	
	public static List<EHealthRecord> statisticByDesc(List<EHealthRecord> eHealthRecords,ArrayList<String> descriptions){
		if(eHealthRecords == null || eHealthRecords.size() == 0 || descriptions == null || descriptions.size() == 0){
			return null;
		}
		List<EHealthRecord> eRecords = new ArrayList<EHealthRecord>();
		for(EHealthRecord e : eHealthRecords){
			String descriString = e.getConditionsdescribed();//描述
			if(check(descriString, descriptions)){
				eRecords.add(e);
			}
			
		}
		return eRecords;
	}
	
	public static boolean check(String desc,ArrayList<String> keywords){
		if (desc == "" || keywords == null || keywords.size() == 0) {
			return false;
		}
		
		for(String s : keywords){
			if(desc.matches(".*" + s + ".*")){
				return true;
			}
		}
		return false;
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		statistics();
	}

}
