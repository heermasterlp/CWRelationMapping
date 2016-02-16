package com.um.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


import com.um.data.DiagClassifyData;

public class MedicineByDescription {
	
	
	
	/**
	 *  convert the array of string to a string.
	 * @param arrays
	 * @return
	 */
	public static String convertArrayToString(String[] arrays){
		if(arrays == null || arrays.length == 0){
			return "";
		}
		String resultString = "";
		for( String s : arrays ){
			resultString += s + ",";
		}
		return resultString;
	}
	
	/**
	 *  
	 * @param arrays
	 * @return
	 */
	public static Map<String, String> convertArraysToMap(String[] arrays){
		if( arrays == null || arrays.length == 0 ){
			return null;
		}
		Map<String, String> result = new HashMap<String, String>();
		for( String a : arrays ){
			String[] split = a.split(":");
			if( split == null || split.length != 2){
				continue;
			}
			result.put(split[0], split[1]);
		}
		return result;
	}
	
	
	
	/**
	 *  格式化病症描述
	 *  	：将描述文本转化成输入格式的描述方式
	 * @param description
	 * @return
	 */
	public static String formattedDescriptionByCount(String description){
		if( description.equals("") ){
			return null;
		}
		String formattedDescriptionString = "";
		//
		Map<String, HashMap<String, ArrayList<String>>> keyworCodeMap = DiagMedicineProcess.creatrReference(DiagClassifyData.descriptionKeywords);
		System.out.println(keyworCodeMap);
		// 3. 根据输入，确定输入编码
		Map<String, String> formattedMap = new HashMap<String, String>();
		Set<String> projectKeySet = keyworCodeMap.keySet();
		// project
		for( String project : projectKeySet ){
			// status
			Set<String> statusSet = keyworCodeMap.get(project).keySet();
			if( statusSet == null || statusSet.size() == 0 ){
				continue;
			}
			
			for( String status : statusSet ){
				int index = 0;
				ArrayList<String> keyArrayList = keyworCodeMap.get(project).get(status);
				int length = keyArrayList.size();
				if( keyArrayList == null || keyArrayList.size() == 0 ){
					continue;
				}
				
				for( String k : keyArrayList ){
					if( description.contains(k)){
						formattedMap.put(status, "1");
					}
					if(index == length - 1 && !formattedMap.containsKey(status)){
						formattedMap.put(status, "0");
					}
					index++;
				}
			}
		}
		// Tanslation
		Map<String, String> descTableMap = MedicineByDescription.convertArraysToMap(DiagClassifyData.descriptionStrings);
		Map<String, String> normalTableMap = MedicineByDescription.convertArraysToMap(DiagClassifyData.normalAndBaddescription);
		Set<String> formattedSet = formattedMap.keySet();
		if( formattedSet == null || formattedSet.size() == 0 ){
			return "";
		}
		for( String f : formattedSet ){
			if(normalTableMap.get(f)==null|| formattedMap.get(f) ==null){
				continue;
			}
			if( normalTableMap.get(f).equals("0") || formattedMap.get(f).equals("0")){
				continue;
			}
			formattedDescriptionString += descTableMap.get(f) + ",";
		}
		
		return formattedDescriptionString;
	}
	
}
