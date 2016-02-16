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
import com.um.util.DiagMedicineProcess;

public class TimeStatusStatistics {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		CWRelationMapping cwRelationMapping = new CWRelationMapping();
		List<EHealthRecord> allData = cwRelationMapping.queryEhealthData();
		
		String shuqianString = "术前";
		String shuhouString = "术后";
		
		String fangliaozhongString = "放疗中";
		String fangliaohouString = "放疗后";
		
		String hualiaozhongString = "化疗中";
		String hualiaohouString = "化疗后";
		
		String fenzibaxiangString = "分子靶向药物";
		String mianyizhiliaoString = "免疫治疗";
		
		String danchun = "单纯中医药治疗";
		
		System.out.println(allData.size());
		
		int count = allData.size();
		
		List<String> paramsList = new ArrayList<String>();
		paramsList.add(shuqianString);
		paramsList.add(shuhouString);
		paramsList.add(fangliaozhongString);
		paramsList.add(fangliaohouString);
		paramsList.add(hualiaozhongString);
		paramsList.add(hualiaohouString);
		paramsList.add(fenzibaxiangString);
		paramsList.add(mianyizhiliaoString);
		paramsList.add(shuhouString + "#" + fangliaozhongString);
		paramsList.add(shuhouString + "#" + fangliaohouString);
		paramsList.add(shuhouString + "#" + hualiaozhongString);
		paramsList.add(shuhouString + "#" + hualiaohouString);
//		paramsList.add(danchun);
		
		List<Map<String, Integer>> results = new ArrayList<Map<String,Integer>>();
		Map<String, Integer> resCountMap = new HashMap<String, Integer>();
		Map<String, Map<String, Integer>> res = new HashMap<String, Map<String,Integer>>();
		// Single medicine
		Map<String, Integer> singleRes = new HashMap<String, Integer>();
		
		for(String s : paramsList){
			List<EHealthRecord> records = new ArrayList<EHealthRecord>();
			
			if (s.contains("#")) {
				String[] ss = s.split("#");
				System.out.println(ss[0] + "--" + ss[1]);
				for(EHealthRecord e : allData){
					if (e.getConditionsdescribed().contains(ss[0].trim()) && e.getConditionsdescribed().contains(ss[1].trim())) {
						records.add(e);
					}
				}
			}else{
				//single 
				for(EHealthRecord e : allData){
					if (e.getConditionsdescribed().contains(s.trim())) {
						records.add(e);
					}
				}
			}
			// 
			if (records.size() == 0) {
				continue;
			}
			
			
			System.out.println(s + " " + records.size());
			resCountMap.put(s, records.size());
			Map<String, Integer> tmp = DiagMedicineProcess.statisEhealthMedicine(records);
			System.out.println(tmp);
			res.put(s, tmp);
			results.add(tmp);
		}
		//单纯中医药治疗
		List<EHealthRecord> singleEhRecords = new ArrayList<EHealthRecord>();
		for(EHealthRecord e : allData ){
			if (!(e.getConditionsdescribed().contains("术前") || e.getConditionsdescribed().contains("术后") ||
					e.getConditionsdescribed().contains("放疗中") ||e.getConditionsdescribed().contains("放疗后") ||
					e.getConditionsdescribed().contains("化疗中") ||e.getConditionsdescribed().contains("化疗后") 
					||e.getConditionsdescribed().contains("分子靶向药物")||e.getConditionsdescribed().contains("免疫治疗"))) {
				singleEhRecords.add(e);
			}
		}
		res.put("单纯中医药治疗", DiagMedicineProcess.statisEhealthMedicine(singleEhRecords));
		resCountMap.put("单纯中医药治疗", singleEhRecords.size());
		System.out.println(results);
		
		
		
		File statText = new File("/Users/heermaster/Documents/file/timeStatusStatistics.csv");
        FileOutputStream is = new FileOutputStream(statText);
        OutputStreamWriter osw = new OutputStreamWriter(is);    
        Writer w = new BufferedWriter(osw);
        
        DecimalFormat df = new DecimalFormat("0.00");
        
        
        Set<String> timeStatusSet = res.keySet();
        for(String t : timeStatusSet){
        	
        	w.write( t + ",");
        	Map<String, Integer> statisMap = res.get(t);
        	if (statisMap.isEmpty()) {
				continue;
			}
        	Set<String> medicineSet = statisMap.keySet();
        	int printNum = 0;
        	for(String m : medicineSet){
        		int num = statisMap.get(m);
        		if (printNum >= 20) {
            		break;
    			}
        		w.write(m + "=" + num + "(" + df.format(100.0 * num / resCountMap.get(t)) + "%)" + ",");
        		printNum++;
        	}
        	w.write("\n");
        }
        
      
        w.close();
		
		
		
		
		
		
		int index = 0;
		for(EHealthRecord e :allData){
			if (e.getConditionsdescribed().contains("术后".trim()) && e.getConditionsdescribed().contains("化疗后".trim())) {
				index++;
			}
			
//			if (e.getConditionsdescribed().contains("分子靶向".trim())) {
//				index++;
//			}
		}
		System.out.println(index);
	}
}
