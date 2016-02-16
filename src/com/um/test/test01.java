package com.um.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.print.attribute.HashAttributeSet;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCollection;
import com.um.classify.DiagnosticsClassify;
import com.um.classify.CWRelationMapping;
import com.um.dao.ConnectionDB;
import com.um.model.ChineseMedicine;
import com.um.model.EHealthRecord;

public class test01 {

	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
//		MongoCollection<Document> collection = ConnectionDB.getCollection("db", "ehealth");
//		
//		 DBObject updateCondition=new BasicDBObject();  
//         
//	        //where name='fox'  
//	        updateCondition.put("ehealthrecord.registrationno", "600025781517");  
//	          
//	        DBObject updatedValue=new BasicDBObject();  
//	        updatedValue.put("ehealthrecord.patientinfo.age", "70岁");  
//	          
//	        DBObject updateSetValue=new BasicDBObject("$set",updatedValue);  
//	        /** 
//	         * update insert_test set headers=3 and legs=4 where name='fox' 
//	         * updateCondition:更新条件 
//	         * updateSetValue:设置的新值 
//	         */  
////	        collection.updateOne(updateCondition, updateSetValue); 
//	        System.out.println(collection.updateOne(updateCondition, updateSetValue));
		
//		String string = "1,1,1,1,0,0,0,0,0,0,0,0,0,0,2,2,1,1,1,0,0,0,0,0,0,0,2,1,0,0,0,0,0,1,1,0,1,1,1,1,1,0,1,0,0,1,1,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,";
//		System.out.println(string.length());
//		String s = string.substring(0, string.length() -1);
//		System.out.println(s);
		
		CWRelationMapping cw = new CWRelationMapping();
        List<EHealthRecord> eHealthRecords = cw.queryEhealthData();
        
        String[] regnoStrings = {"600028284157","600026037526" ,"600030468581" ,"600029999983" ,"600027478640" ,"600026107490" ,"600027382085" ,"600027348611" ,"600028618410" ,"600028938223" ,"600029283644" };
         for(String s: regnoStrings){
              System. out.print( s + " : ");
               for(EHealthRecord e : eHealthRecords){
                     if( e.getRegistrationno().equals( s.trim())){
                           if( e.getChineseMedicines() != null && e.getChineseMedicines().size() >= 0){
                                List<ChineseMedicine > cm = e.getChineseMedicines();
                                 for(ChineseMedicine c: cm){
                                      System. out.print( c.getNameString() + ",");
//                                      System.out.println(e.getConditionsdescribed());
                                }
                          }
                    }
                     
              }
               System.out.println();
        }
		
	}
	

}
