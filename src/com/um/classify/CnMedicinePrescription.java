package com.um.classify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.um.classify.CWRelationMapping;
import com.um.data.DiagClassifyData;
import com.um.model.EHealthRecord;
import com.um.util.DiagMedicineProcess;

/**
 *  功能： 根据用户输入的病症，基于统计和规则，得到相对应的中药处方，输出；
 *  	输入：  用户的中医病症描述
 *  	输出：  对应的中药处方
 * @author lp
 *
 */
public class CnMedicinePrescription {

	private List<EHealthRecord> allEHealthRecords; // 全部病历
	
	public CnMedicinePrescription(List<EHealthRecord> eHealthRecords){
		this.allEHealthRecords = eHealthRecords;
	}
	
	/**
	 *  基于病症描述，得出中医处方
	 * @param diagString
	 * @return
	 */
	public List<String> getCnMedicines(String diagString){
		if(diagString == ""){
			return null;
		}
		
		List<String> medicieList = new ArrayList<String>();
		
		int threshold = 15; // 中药处方阈值：预计输出15味中药
		int currentCount = 0; // 当前的中药处方数量
		
		// 1. 根据中医描述，提取全部病例的中医描述关键字
		List<String> cnkeywords = DiagMedicineProcess.arrayToList(DiagClassifyData.cndescriclassify);
		
		/**
		 *  1. 统计所有的中药处方，并排序
		 */
		// 所有的中药处方统计 <名称，数量>
		Map<String, Integer> allMedicineMap = DiagMedicineProcess.statisEhealthMedicine(allEHealthRecords);
		System.out.println(allMedicineMap);
		allMedicineMap = DiagMedicineProcess.sortMapByValue(allMedicineMap); // 统计结果排序
		System.out.println(allMedicineMap);
		/**
		 *  2. 找出出现概率大于90%的，作为结果 
		 */
		int allRecordLength = allEHealthRecords.size(); // 全部病历的数量
		double percent = 0.9;
		List<String> medicineWithNinePercent = DiagMedicineProcess.statisMedicineNinePercent(allMedicineMap, allRecordLength, percent);
		medicieList.addAll(medicineWithNinePercent);
//		System.out.println(medicineWithNinePercent);
		
		// 去掉出现概率较大的
		allMedicineMap = DiagMedicineProcess.removeMapOfList(allMedicineMap, medicineWithNinePercent);
//		System.out.println(allMedicineMap);
		
		/**
		 * 	3. 累计后续中药，判断累加结果是否大于90%，根据判断规则输出
		 */
		// 3.1、根据中医描述关键字，对病例分类
		Map<EHealthRecord, String> ehealthMap = DiagMedicineProcess.classifyEhealthMap(cnkeywords, allEHealthRecords);
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
				cm.setKeywords(DiagMedicineProcess.stringToArray(strings[0]));
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
		
//		System.out.println("[分类 size] :" + cmDescList.size());
		
		// 2. 识别输入文字，确定分类类型
		CMDescriptionClassify cmDescriptionClassify = DiagMedicineProcess.matchDescriptionClassify(diagString, cmDescList);// 确定描述分类
		if(cmDescriptionClassify == null){
			return null;
		}
//		System.out.println("[分类类型]:" + cmDescriptionClassify.getDescriptionString());
		
		// 3. 统计该类型中的中药处方---统计属于该类型的病历中的所有中药处方情况
		Map<String, Integer> cnmedicineMap = new HashMap<String, Integer>();
		
		if(cmDescriptionClassify != null && cmDescriptionClassify.geteHealthRecords() != null &&
				cmDescriptionClassify.geteHealthRecords().size() > 0){
			cnmedicineMap = DiagMedicineProcess.statisMedicineWithCMDescription(cmDescriptionClassify);
		}
//		System.out.println("map :" + cnmedicineMap.toString());
		// 4. 输出中药处方
		
		Set<String> cnmedicienSet = cnmedicineMap.keySet(); // 统计与输入描述相符合类型的中药名称
		
		double percent1 = 0.9; // 100%
		currentCount = medicieList.size(); // 当前拥有的中药处方数量
		while(currentCount <= threshold){
			// 中药处方数量不足threshold
			// 1. 累计计算，得出一组中药(判断这组中药的并集的出现概率 大于100%)
			List<String> accumulateList = DiagMedicineProcess.accumulateMedicines(allMedicineMap, allEHealthRecords, percent1); // 统计累计出现概率之和大于percent的多为中药
			if(accumulateList == null || accumulateList.isEmpty()){
				break; // 剩下的中药不足以构成分组
			}
			
			Map<String, Integer> calculMap = new LinkedHashMap<String,Integer>(accumulateList.size()); // 改组中药的中药名称以及数量
			for(String s : accumulateList){
				calculMap.put(s, allMedicineMap.get(s)); // 暂存中药与出现次数 map
			}
			// 去掉已经选择的中药
			allMedicineMap = DiagMedicineProcess.removeMapOfList(allMedicineMap, accumulateList); 
			
			// 2. 计算这些中药之间的相关性，并根据规则进行分析
			
			
			// 3. 根据中医描述，对病例进行统计，
			
			
			// 4. 确定需要输出的中药，取与描述所统计的中医组合取并集
			Set<String> unionSet = new HashSet<String>();
			for(String s : accumulateList){
				if(cnmedicienSet.contains(s)){
					unionSet.add(s);
				}
			}
			
			medicieList.addAll(unionSet);
			
			currentCount = medicieList.size();
		}
		
		/**
		 * 	4. 整理结果，并输出最终结果
		 */
		
		return medicieList;
	}
	
	
	
	
	
	
	
	// get and set

	public List<EHealthRecord> getAllEHealthRecords() {
		return allEHealthRecords;
	}

	public void setAllEHealthRecords(List<EHealthRecord> allEHealthRecords) {
		this.allEHealthRecords = allEHealthRecords;
	}
	
	public static void main(String[] agrvs){
		
		CWRelationMapping cwRelationMapping = new CWRelationMapping();
		List<EHealthRecord> allRecords = cwRelationMapping.queryEhealthData();
		
		if(allRecords == null || allRecords.size() == 0){
			return ;
		}
		
		String diagString = "现仍气短，下肢酸胀缓解，纳眠可，二便调。舌质红，舌苔白，脉细";
		System.out.println("输入症状 ： " + diagString);
		CnMedicinePrescription cnp = new CnMedicinePrescription(allRecords);
		
		List<String> resultStrings = cnp.getCnMedicines(diagString); // 查询，生成结果
		
		if(resultStrings == null || resultStrings.size() == 0){
			return;
		}
		System.out.println("------------------中药处方--------------------");
		for(String s : resultStrings){
			System.out.println(s);
		}
		System.out.println("----------------------------------------------");
		
	}
}
