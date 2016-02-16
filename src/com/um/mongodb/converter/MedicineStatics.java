package com.um.mongodb.converter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bson.Document;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
/**
 *
 * @author lp
 */
public class MedicineStatics {
    /**
     * 
     * @param doc
     * @return
     */
    
    public static List<String> getChineseMedList(Document doc){
    	if(doc == null){
    		return null;
    	}
    	
    	List<String> resultList = new ArrayList<String>();
    	
    	Document ehealthDocument = (Document) doc.get("ehealthrecord");   	
    	
    	
    	Document medicines = null;
    	if(ehealthDocument.get("medicine") instanceof Document){
    		medicines = (Document) ehealthDocument.get("medicine");
    	}
    	
    	Document chineseMedicines = null;
    	if (medicines != null) {
    		if(medicines.get("chineseMedicines") instanceof Document){
        		chineseMedicines = (Document) medicines.get("chineseMedicines");
        	}
		}else{
			return null;
		} 	
    	
    	
    	List<Document> chineseList = null;
    	
    	if(chineseMedicines != null){
    		if(chineseMedicines.get("chineseMedicine") != null){
        		if(chineseMedicines.get("chineseMedicine") instanceof Document){
            		//
            		Document document = (Document) chineseMedicines.get("chineseMedicine");
            		resultList.add(document.getString("cname"));
            	}else{
            		chineseList = (List<Document>) chineseMedicines.get("chineseMedicine");
            		if(chineseList != null && chineseList.size() > 0){
                		for(Document d : chineseList){
                    		resultList.add(d.getString("cname"));
                    	}
                	}
            	}
        	}else{
        		return null;
        	}
    	}
    	   	
    	return resultList;
    	
    }
    
    public static HashMap<String, Integer> staticsChineseMedicine(List<String> list){
    	if(list == null || list.size() == 0){
    		return null;
    	}
    	
    	HashMap<String, Integer> results = new HashMap<String, Integer>();
    	
    	//统计
    	results.put(list.get(0), 1);
//    	System.out.println("[0]" + list.get(0));
//    	System.out.println(results.toString());
    	for(int i = 1; i < list.size(); i++){
    		results = statics(list.get(i).trim(),results);
    	}
        
        results = sortMap(results);
        
    	return results;
    } 
    
    public static HashMap<String, Integer> statics(String string,HashMap<String, Integer> tample){
    	
    	boolean flag = false;
    	
    	for(int i=0;i<tample.size();i++){
    		    		
    		if(tample.get(string) != null){
    			// 在里面
    			int count = tample.get(string);
    			count++;
    			tample.remove(string);
    			tample.put(string, count);
    			flag = true;
                        break;
    		}    		
    	}
    	if(!flag){
    		//不在
    		tample.put(string, 1);
    	}
//    	System.out.println(string + ":" + tample.get(string));
    	return tample;
    }
    
    public static HashMap<String,Integer> sortMap(HashMap<String,Integer> map){
         if(map == null){
            return null;
        }
        HashMap<String,Integer> resultMap = new HashMap<String,Integer>();
        
        List<Map.Entry<String, Integer>> listData = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
        
        //排序
        Collections.sort(listData, new Comparator<Map.Entry<String, Integer>>(){
        	public int compare(Map.Entry<String,Integer > o1, Map.Entry<String, Integer> o2){
        			return (o2.getValue() - o1.getValue());
        		}
        	}
        );
        
        
        int length = listData.size();
        
        for(int i = 0;i < length;i++){
        	resultMap.put(listData.get(i).getKey().toString(), listData.get(i).getValue());
        }
        
        return resultMap;
    }
    
    public static List<Map.Entry<String, Integer>> sortList(HashMap<String,Integer> map){
         if(map == null){
            return null;
        }
        HashMap<String,Integer> resultMap = new HashMap<String,Integer>();
        
        List<Map.Entry<String, Integer>> listData = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
        
        //排序
        Collections.sort(listData, new Comparator<Map.Entry<String, Integer>>(){
        	public int compare(Map.Entry<String,Integer > o1, Map.Entry<String, Integer> o2){
        			return (o2.getValue() - o1.getValue());
        		}
        	}
        );
               
        return listData;
    }
}
