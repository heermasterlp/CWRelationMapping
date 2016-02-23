package com.um.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.Document;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.um.classify.CWRelationMapping;
import com.um.dao.ConnectionDB;
import com.um.data.DiagClassifyData;
import com.um.model.ChineseMedicine;
import com.um.model.EHealthRecord;
import com.um.mongodb.converter.EhealthRecordConverter;
import com.um.util.DiagMedicineProcess;

public class PreprocessData {
	//数据预处理－－－将数据处理成机器学习的数据
	
	
	
	/**
	 *  获取前60味中药
	 * @param eRecords
	 * @return
	 */
	public static List<String> getMedicineList(List<EHealthRecord> eRecords){
		if(eRecords == null || eRecords.isEmpty()){
			return null;
		}
		
		Map<String, Integer> allMedicinesMap = DiagMedicineProcess.statisEhealthMedicine(eRecords);
		allMedicinesMap = DiagMedicineProcess.sortMapByValue(allMedicinesMap);
		
		Set<String> medicineNameSet = allMedicinesMap.keySet();
		List<String> medicinelList = new ArrayList<String>();
		medicinelList.addAll(medicineNameSet);
		
		
		System.out.println("medicine size:" + medicinelList.size());
		return medicinelList;
	}
	
	
	/**
	 *  初始化描述对比表
	 * @return
	 */
	public static Map<String, HashMap<String, ArrayList<String>>> getDescriptionMap(){
		String[] descriptionStrings = DiagClassifyData.descriptionKeywords1;
		Map<String, HashMap<String, ArrayList<String>>> descriptionMap = new HashMap<String, HashMap<String,ArrayList<String>>>();
		HashMap<String, ArrayList<String>> descHashMap = null;
		for(String s : descriptionStrings){
			descHashMap = new HashMap<String, ArrayList<String>>();
			String[] tmpStrings = s.split("%");  // 0:项目  1:描述
			String[] descStrings = tmpStrings[1].split("#");
			for(String ss : descStrings){
				if(ss.split(":").length == 2){
					String[] dStrings = ss.split(":");
					String[] listStrings = dStrings[1].split("\\|");
					ArrayList<String> list = new ArrayList<String>();
					for(String sss : listStrings){
						list.add(sss);
					}
					descHashMap.put(dStrings[0], list);
				}
			}
			descriptionMap.put(tmpStrings[0], descHashMap);
		}
		return descriptionMap;
	}
	
	
	/**
	 *  诊断关键字
	 * @return
	 */
	public static List<String> getDiagnoseKeywords(){
		List<String> keywordsList = new ArrayList<String>();
		// 	1. key word of all records 
		String[] keysList = DiagClassifyData.cnDiagCodeStrings;
		String[] tmpStrings = null;
		for(String s : keysList){
			tmpStrings = s.split("\\|");
			keywordsList.add(tmpStrings[0]);
		}
		
		return keywordsList;
	}
	
	
	public static void main(String[] args) throws IOException, BadHanyuPinyinOutputFormatCombination {
		// TODO Auto-generated method stub
		List<String> titleList = new ArrayList<String>(); // 标题
//		titleList.add("批次");
		// 1. 读取病例
		
		List<EHealthRecord> allHealthRecords = new ArrayList<EHealthRecord>() ; // All records data
		
		MongoCollection<Document> collection = ConnectionDB.getCollection("db", "ehealthdata");
		
		String batchString = "2012";
		Document conditions = new Document();
		conditions.append("ehealthrecord.batch", batchString);
		
		FindIterable<Document> iterable = collection.find(conditions);
		if(iterable == null){
			return ;
		}
		iterable.forEach(new Block<Document>() {

			@Override
			public void apply(Document document) {
				// TODO Auto-generated method stub
				EHealthRecord eHealthRecord = EhealthRecordConverter.toEHealthRecord(document);
				allHealthRecords.add(eHealthRecord);
			}
			
		});
		
		System.out.println("all records size: " + allHealthRecords.size());
//		List<EHealthRecord> allHealthRecords = cwRelationMapping.queryEhealthDataByCollection("ehealthdata");
		// 2. 诊断关键字
		List<String> diagnoseKeyWords = getDiagnoseKeywords();
		titleList.addAll(diagnoseKeyWords);
		System.out.println(diagnoseKeyWords);
		// 3. 描述关键字
		Map<String, HashMap<String, ArrayList<String>>> descriptionTableMap = getDescriptionMap();
		titleList.addAll(descriptionTableMap.keySet());
		System.out.println(descriptionTableMap);
		// 4. 60味中药
		List<String> medicinelList = getMedicineList(allHealthRecords);
		titleList.addAll(medicinelList);
		System.out.println(medicinelList);
		
		Map<EHealthRecord, HashMap<String, String>> processResult = new HashMap<EHealthRecord, HashMap<String,String>>();
		HashMap<String, String> dataMap = null;
		for(EHealthRecord e : allHealthRecords){
			dataMap = new HashMap<String, String>();
			// 1. 判断诊断，有关键字，则1  没有则为0
			String diagnoseString = e.getChinesediagnostics(); // 中医诊断
			for(String keyword : diagnoseKeyWords){
				if(diagnoseString.matches(".*" + keyword + ".*")){
					dataMap.put(keyword, "1");
				}else{
					dataMap.put(keyword, "0");
				}
			}
			
			
			//2. 描述
			
			String descriptionString = e.getConditionsdescribed(); // 描述
			Set<String> project = descriptionTableMap.keySet();
			HashMap<String, ArrayList<String>> desHashMap = null;
			for(String descString : project){
				String valueString = "0";
				desHashMap = descriptionTableMap.get(descString);
				Set<String> desSet = desHashMap.keySet();
				for(String s:desSet){
					ArrayList<String> keywordList = desHashMap.get(s);
					for(String key: keywordList){
						if(descriptionString.matches(".*" + key + ".*")){
							// 匹配
							valueString = s;
							break;
						}
					}
				}
				dataMap.put(descString, valueString);
			}
			
			// 3. 中药
			if(e.getChineseMedicines() != null && e.getChineseMedicines().size() > 0){
				Set<String> medicineSet = new HashSet<String>();
				for(ChineseMedicine cMedicine : e.getChineseMedicines()){
					medicineSet.add(cMedicine.getNameString());
				}
				
				for(String s : medicinelList){
					if(medicineSet.contains(s)){
						dataMap.put(s, "1");
					}else{
						dataMap.put(s, "0");
					}
				}
			}
			
			processResult.put(e, dataMap);
		}
		// display
		
		/**
		 *  arff文件
		 */
		File statText = new File("/Users/peterliu/Documents/file/statsTest.arff");
        FileOutputStream is = new FileOutputStream(statText);
        OutputStreamWriter osw = new OutputStreamWriter(is);    
        Writer w = new BufferedWriter(osw);
        
        w.write("@relation ehealthrecord\n");
        for(String s:titleList){
        	System.out.println(s);
        	if (!s.contains("水煎")) {
        		w.write("@attribute " + DiagMedicineProcess.getPinyinString(s) + " numeric\n");
			}
        }
        w.write("\n");
        w.write("@data\n");
		Set<EHealthRecord> eSet = processResult.keySet();
		for(EHealthRecord e: eSet){
//			String dataString = e.getBatchString() + ",";
			String dataString = "";
			for(String s:titleList){
				if(processResult.get(e) != null && processResult.get(e).get(s) != null){
					dataString += processResult.get(e).get(s) + ",";
				}
				
			}
			String string = dataString.substring(0, dataString.length() - 1);
			string += "\n";
			w.write(string);
		}
		w.flush();
		System.out.println("arff file successed!");
		
		/**
		 *  csv 文件
		 */
		statText = new File("/Users/peterliu/Documents/file/statsTest.csv");
        is = new FileOutputStream(statText);
        osw = new OutputStreamWriter(is);    
        w = new BufferedWriter(osw);
        for(String s: titleList){
        	if (!s.contains("水煎")) {
        		w.write(DiagMedicineProcess.getPinyinString(s) + ",");
			}
        }
        w.write("\n");
        for(EHealthRecord e: eSet){
//        	w.write(e.getBatchString() + ",");
			for(String s:titleList){
				if(processResult.get(e) != null && processResult.get(e).get(s) != null){
					w.write( processResult.get(e).get(s)+",");
				}
				
			}
			
			w.write("\n");
		}
        w.flush();
        System.out.println("csv file successed!");

        /**
         *  转换表
         */
        statText = new File("/Users/peterliu/Documents/file/tranlations.csv");
        is = new FileOutputStream(statText);
        osw = new OutputStreamWriter(is);    
        w = new BufferedWriter(osw);
        for(String s: titleList){
        	w.write(s + ",");
        }
        w.write("\n");
        for(String s: titleList){
        	if (!s.contains("水煎")) {
        		w.write(DiagMedicineProcess.getPinyinString(s) + ",");
        	}
        }
        w.close();
        System.out.println("translation file successed!");
	}

}
