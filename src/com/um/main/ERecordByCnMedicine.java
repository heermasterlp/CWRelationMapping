package com.um.main;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.um.classify.CWRelationMapping;
import com.um.model.ChineseMedicine;
import com.um.model.EHealthRecord;

/**
 *  根据中药名称查询病历信息
 * @author lp
 *
 */
public class ERecordByCnMedicine {

	/**
	 *  根据中药名称，查询有该味中药的病历信息
	 * @param name
	 * @return
	 */
	public static ArrayList<EHealthRecord> queryByCnMedicine(String name){
		if(name == ""){
			return null;
		}
		ArrayList<EHealthRecord> resutList = new ArrayList<EHealthRecord>();
		Set<EHealthRecord> ehealSet = new HashSet<EHealthRecord>();
		// 1. 查询全部病历
		CWRelationMapping cMapping = new CWRelationMapping();
		List<EHealthRecord> allRecords = cMapping.queryEhealthData();
		
		// 2. 根据名称查询相对应的病历
		
		if(allRecords == null || allRecords.size() == 0){
			return null;
		}
		
		for(EHealthRecord e : allRecords){
			if(e.getChineseMedicines() == null || e.getChineseMedicines().size() == 0){
				continue;
			}
			for(ChineseMedicine c : e.getChineseMedicines()){
				if(c.getNameString().equals(name)){
					ehealSet.add(e);
				}
			}
		}
		for(EHealthRecord e : ehealSet){
			resutList.add(e);
		}
		return resutList;
	}
	
	/**
	 *  查询同时出现多味中药的病历情况
	 * @param names
	 * @return
	 */
	public static List<EHealthRecord> queryWithMedicineList(String[] names){
		if(names == null || names.length == 0){
			return null;
		}
		
		List<EHealthRecord> results = null;
		
		// 1、获取全部病历数据
		CWRelationMapping cMapping = new CWRelationMapping();		
		List<EHealthRecord> allRecrods = cMapping.queryEhealthData();
		if(allRecrods == null || allRecrods.size() == 0){
			return null;
		}
		
		results = new ArrayList<EHealthRecord>();
//		Set<EHealthRecord> ehealSet = new HashSet<EHealthRecord>();
		
		// 2、判断是否同时出现在同一病历中
		for(EHealthRecord e : allRecrods){
			if(hasThisMedicine(e, names)){
				//同时出现在同一病历中
				results.add(e);
//				ehealSet.add(e);
			}
		}
		
//		for(EHealthRecord e : ehealSet){
//			results.add(e);
//		}
		
		return results;
	}
	
	/**
	 *  查询同时出现多味中药的病历情况
	 * @param names
	 * @return
	 */
	public static List<EHealthRecord> queryWithMedicineList(String[] names,List<EHealthRecord> allRecrods){
		if(names == null || names.length == 0){
			return null;
		}
		
		List<EHealthRecord> results = null;
		// 1、获取全部病历数据
		if(allRecrods == null || allRecrods.size() == 0){
			return null;
		}
		
		results = new ArrayList<EHealthRecord>();
		
		// 2、判断是否同时出现在同一病历中
		for(EHealthRecord e : allRecrods){
			if(hasThisMedicine(e, names)){
				//同时出现在同一病历中
				results.add(e);
			}
		}
		
		for(EHealthRecord e : results){
			System.out.println(e.getRegistrationno());
		}
		
		return results;
	}
	
	/**
	 *  判断是否同时出现所有的中药
	 * @param e
	 * @param names
	 * @return
	 */
	public static boolean hasThisMedicine(EHealthRecord e,String[] names){
		if(e == null || e.getChineseMedicines() == null || 
				e.getChineseMedicines().size() == 0 || names == null || names.length == 0){
			return false;
		}
		
		List<ChineseMedicine> allMedicines = e.getChineseMedicines(); // 中药处方
		boolean hasMedicine = true;
		boolean flag = false;
		int length = allMedicines.size();
		for(String s : names){
			flag = false;
			for(int i = 0; i < length; i++){
				if(s.equals(allMedicines.get(i).getNameString())){
					flag = true;// 同时出现则为true = 只要有一个不出现就为false 
				}
				if(!flag && i == length - 1){
					flag = false;
				}
			}
			hasMedicine = hasMedicine && flag;
		}
		
		return hasMedicine;
	}
	
	
	
	/**
	 *  统计array中 中药组合在病历中出现情况
	 *  	
	 *  	1、确定所有可能的中药名称组合
	 *  	2、查询每一种中药名称在病历中出现的概率情况
	 *  
	 * @param medicines
	 * @return
	 */
	public static List<String> statisWithAllMedicines(String[] medicines){
		if(medicines == null || medicines.length == 0){
			return null;
		}
		List<String> results = new ArrayList<String>();
		//1. 确定所有的中药组合
		List<String[]> medicineComb = fullSort(medicines); // 所有的中药组合
		
		//2. 查询每一种中药组合出现的概率
		CWRelationMapping cwRelationMapping = new CWRelationMapping();
		List<EHealthRecord> allRecrods = cwRelationMapping.queryEhealthData(); // 全部病历
		
		List<ArrayList<EHealthRecord>> statisResult = new ArrayList<ArrayList<EHealthRecord>>();
		
		for(String[] medStrings : medicineComb){
			ArrayList<EHealthRecord> tmps = new ArrayList<EHealthRecord>();
			// 2.2、判断是否同时出现在同一病历中
			for(EHealthRecord e : allRecrods){
				if(hasThisMedicine(e, medStrings)){
					//同时出现在同一病历中
					tmps.add(e);
				}
			}
			statisResult.add(tmps); // 统计结果
		}
		// 3、整理统计结果
		if(statisResult == null || statisResult.size() == 0){
			return null;
		}
		
		int allLen = allRecrods.size(); // 全部病历的长度
		DecimalFormat df = new DecimalFormat("0.00");
		
		for( int i = 0; i < statisResult.size(); i++ ){
			if(statisResult.get(i) != null && statisResult.get(i).size() > 0){
				// 有统计结果
				String string = ""; 
				for(String s : medicineComb.get(i)){
					string += s + " "; //中药名称
				}
				string += "  :  ";//统计结果
				string += statisResult.get(i).size() + "        "; // 出现次数
				string += df.format(100.0 * statisResult.get(i).size() / allLen) + "%";
				results.add(string);
			}
		}
		
		return results;
	}
	
	/**
	 *  字符的排列
	 * @param strings
	 * @return
	 */
	public static List<String[]> fullSort(String[] strings){
		if(strings == null || strings.length == 0){
			return null;
		}
		//1. 确定所有的中药组合
		List<String[]> results = new ArrayList<String[]>(); // 所有的中药组合
		
		int length = strings.length;
		
		for(int i = 2; i <= length; i++){
			// 每一组组合的长度
			for(int j = 0; j < length + 1 - i; j++){
				String[] s = new String[i];
				for(int k = 0; k < i; k++){
					s[k] = strings[j + k];
				}
				results.add(s);
			}
		}
		return results;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// 1、判断有某一味中药的病历
		String name = "炙黄芪";
		List<EHealthRecord> queryList = queryByCnMedicine(name);
		System.out.println(queryList.size());
		
		// 2、 判断同时有多味中药的所有病历
		
		String[] names = {"党参","太子参"};
		List<EHealthRecord> erecordWithMedicines = queryWithMedicineList(names);
		System.out.println(erecordWithMedicines.size());
		for(EHealthRecord e : erecordWithMedicines){
			System.out.println(e.getRegistrationno());
		}
		
		
		// 3, 判断前12-20味中药出现的情况
//		String[] prenames = {"莪术","蛇莓","猫爪草","望江南子","山慈菇","延胡索","连翘","白茅根","制川乌"};
//		
//		List<String> re = statisWithAllMedicines(prenames);
//		for(String s : re){
//			System.out.println(s);
//		}
		
//		String s1 = "白术";
//		String s2 = "炙黄芪";
//		List<EHealthRecord> res = new ArrayList<EHealthRecord>();
//		boolean b1 = false;
//		boolean b2 = false;
//		
//		CWRelationMapping  c = new CWRelationMapping();
//		List<EHealthRecord> all = c.queryEhealthData();
//		
//		
//		for(EHealthRecord e : all){
//			b1 = false;
//			b2 = false;
//			if(e.getChineseMedicines() != null){
//				for(ChineseMedicine cm : e.getChineseMedicines()){
//					if(cm.getNameString().equals(s1)){
//						b1 = true;
//					}
//					if(cm.getNameString().equals(s2)){
//						b2 = true;
//					}
//
//				}
//				if(b1 && b2){
//					res.add(e);
//				}
//			}
//			
//			
//		}
//		System.out.println(res.size());
		
		
	}

}
