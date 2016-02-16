package com.um.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.um.classify.CMDescriptionClassify;
import com.um.classify.CWRelationMapping;
import com.um.model.ChineseMedicine;
import com.um.model.EHealthRecord;
import com.um.mongodb.converter.MedicineStatics;

public class CDMedicineStatis {
	/**
	 *  根据中医病症描述，统计中药处方
	 *  	
	 *  	原则： 什么症状对应什么药方
	 *  	
	 *  	功能： 1、分析中医病症描述，提取关键字，并依据关键字对中医描述进行分类；
	 *  		 2、诊断分类分别进行中药处方统计；
	 * @param args
	 */
	
	/**
	 *  统计每种类型的中药处方 ： 名称 + 数量
	 * @param cmd
	 * @return
	 */
	public static HashMap<String, Integer> statisMedicines(CMDescriptionClassify cmd){
		if(cmd == null || cmd.geteHealthRecords() == null || cmd.geteHealthRecords().size() == 0){
			return null;
		}
		List<EHealthRecord> eHealthRecords = cmd.geteHealthRecords(); // 病历 list
		// 1. 获取全部的中药名称
		List<String> allMedicineName = new ArrayList<String>();
		
		for(EHealthRecord e : eHealthRecords){
			if(e.getChineseMedicines() != null && e.getChineseMedicines().size() > 0){
				for(ChineseMedicine c : e.getChineseMedicines()){
					allMedicineName.add(c.getNameString());
				}
			}
		}
		
		// 2. 对中医名称统计重复数量
		HashMap<String, Integer> results = MedicineStatics.staticsChineseMedicine(allMedicineName);
		return results;
	}
	
	/**
	 *  对病历数据进行分类，并对类型进行编码
	 * @param keywords
	 * @param eHealthRecords
	 * @return
	 */
	public static Map<EHealthRecord, String> classifyEhealthMap(List<String> keywords,List<EHealthRecord> eHealthRecords){
		if(keywords == null || keywords.size() == 0 || eHealthRecords == null || eHealthRecords.size() == 0){
			return null;
		}
		Map<EHealthRecord, String> resultMap = new HashMap<EHealthRecord, String>();
		
		for(EHealthRecord e : eHealthRecords){
			String descString = "";
			String codeString = "";
			for(String k : keywords){
				// xx|k1
				String[] strings = k.split("\\|");
				if(e.getConditionsdescribed().matches(".*" + strings[0] + ".*")){
					descString += strings[0] + " ";
					codeString += strings[1] + " ";
				}
			}
			if(descString != "" && codeString != ""){
				resultMap.put(e, descString + "|" + codeString);
			}
		}
		
		return resultMap;
	}
	
	/**
	 *  根据描述，匹配类型
	 * @param desc
	 * @param cmdList
	 * @return
	 */
	public static CMDescriptionClassify mathDescriptionClassify(String desc,List<CMDescriptionClassify> cmdList){
		if(desc == "" || cmdList == null || cmdList.size() == 0){
			return null;
		}
		// 匹配
		
		int maxcount = 0; // 最大匹配长度		
		int index = -1;    // cmdList 索引		
		int length = cmdList.size();
		
		for(int i = 0; i < length; i++ ){
			
			int count = 0;    // 匹配长度			
			String[] keywords = cmdList.get(i).getKeywords(); // 关键字
			
			for(String s : keywords ){
				if(desc.matches(".*" + s + ".*")){
					count++;
				}				
			}
			if(count > maxcount){
				maxcount = count;
				index = i;
			}
		}
		
		if(index != -1){
			return cmdList.get(index);
		}else{
			return null;
		}
	}
	
	/**
	 * 读取文件，生成关键字组
	 * @param file
	 * @return
	 * @throws IOException 
	 */
	public static List<String> createKeyWords(File file) throws IOException{
		if(file == null ){
			return null;
		}
		
		List<String> keywords = new ArrayList<String>();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		
		String contetns = "";
		while((contetns = reader.readLine()) != null){
			keywords.add(contetns);
		}
		reader.close();
		return keywords;
	}
	/**
	 *  string to array （split with 空格）
	 * @param string
	 * @return
	 */
	public static String[]  stringToArray(String string){
		if(string == ""){
			return null;
		}
		if(string.split(" ") != null){
			return string.split(" ");
		}
		return null;
	}
	
	public static void test(String string) throws IOException{
		File chineseDiagFile = new File("D:\\project\\document\\cnmedicinecode.txt");
		
		List<String> keywords = createKeyWords(chineseDiagFile);
		
		String descString = "";
		String codeString = "";
		for(String k : keywords){
			// xx|k1
			String[] strings = k.split("\\|");
			if(string.matches(".*" + strings[0] + ".*")){
				descString += strings[0] + " ";
				codeString += strings[1] + " ";
			}
		}
		
		System.out.println("关键字组:" + descString);
		System.out.println("编码：" + codeString);
	}
	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// 1、构建关键字组
		File chineseDiagFile = new File("D:\\project\\document\\cnmedicinecode.txt");
		
		List<String> keywords = createKeyWords(chineseDiagFile);
		
		System.out.println(keywords.toString());
		System.out.println(keywords.size());
		// 2、读取病例数据
		CWRelationMapping cwRelationMapping = new CWRelationMapping();
		List<EHealthRecord> eHealthList = cwRelationMapping.queryEhealthData();
		// 3、病例分类
		
		Map<EHealthRecord, String> ehealthMap = classifyEhealthMap(keywords, eHealthList);
		
		System.out.println(ehealthMap.size());
		
		List<CMDescriptionClassify> cmDescList = new ArrayList<CMDescriptionClassify>(); // 分类列表
		
		Set<String> descCodeSet = new TreeSet<String>(); // 编码集合
		Set<EHealthRecord> sets = ehealthMap.keySet();
		
		for(EHealthRecord e: sets){
			if(descCodeSet.add(ehealthMap.get(e))){
				// 新的编码类型
				CMDescriptionClassify cm = new CMDescriptionClassify();
				String[] strings = ehealthMap.get(e).split("\\|");
				cm.setDescriptionString(strings[0]);
				cm.setDescriptionCode(strings[1]);
				cm.setKeywords(stringToArray(strings[0]));
				cm.geteHealthRecords().add(e);
				
				cmDescList.add(cm);
			}else{
				// set中已经有了该类型的编码
				String[] strings = ehealthMap.get(e).split("\\|");
				if(cmDescList != null && cmDescList.size() > 0){
					for(CMDescriptionClassify c : cmDescList){
						if(c.getDescriptionCode().equals(strings[1])){
							c.geteHealthRecords().add(e);
						}
					}
				}
			}
		}
		
		System.out.println("[cmd list] :" + cmDescList.size());
		System.out.println("[code set] :" + descCodeSet.size());
//		System.out.println(descCodeSet.toString());
		
		// 4、统计分类之后的病历中药处方
		
		Map<CMDescriptionClassify, HashMap<String, Integer>> cmdStatisMap = new HashMap<CMDescriptionClassify, HashMap<String,Integer>>();
		
		if(cmDescList != null &&cmDescList.size() > 0){
			for(CMDescriptionClassify c : cmDescList){
				HashMap<String, Integer> medicineMap = statisMedicines(c);
				cmdStatisMap.put(c, medicineMap);
			}
		}
		System.out.println(cmdStatisMap.size());
		
//		Set<CMDescriptionClassify> cmdSet = cmdStatisMap.keySet();
//		for(CMDescriptionClassify c: cmdSet){
//			System.out.println(cmdStatisMap.get(c));
//		}
		
		// 5. 根据描述，输出中药处方
		
		String descString = "纳眠可 二便调 舌质红 舌苔黄 舌苔黄腻 脉弦滑 胸闷少气";
		CMDescriptionClassify matchCmd = mathDescriptionClassify(descString, cmDescList);
		System.out.println(matchCmd.getDescriptionCode());
		System.out.println(cmdStatisMap.get(matchCmd));
		
		Set<String> medicineNameSet = cmdStatisMap.get(matchCmd).keySet();
		System.out.println(medicineNameSet);
		System.out.println(matchCmd.geteHealthRecords().size());
		System.out.println(matchCmd.geteHealthRecords().get(0).getRegistrationno());
	}

}
