package com.um.mongodb.converter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.um.model.EHealthRecord;

import org.bson.BSONObject;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.um.model.ChineseMedicine;
import com.um.model.PatientInfo;
import com.um.model.WesternMedicine;
/**
 *
 * @author lp
 */
public class EhealthRecordConverter {
    
    /**
     *  convert ehealthrecord to bsonobject
     * @param eHealthRecord
     * @return 
     */
    public static BSONObject toBSONObject(EHealthRecord eHealthRecord){
        if(eHealthRecord == null){
            return null;
        }
        BSONObject bSONObject = null;
        return bSONObject;
    }
    
    /**
     *  convert bsonobject to ehealthrecord
     * @param bSONObject
     * @return 
     */
    @SuppressWarnings("unchecked")
	public static EHealthRecord toEHealthRecord(Document bSONObject){
        if(bSONObject == null){
            return null;
        }
        ObjectId _id = (ObjectId) bSONObject.get("_id");
        
        Document ehealrecordDocument = (Document) bSONObject.get("ehealthrecord");
        
        Document patientInfoDoc = (Document) ehealrecordDocument.get("patientinfo");
        
        Document medicines = null;
        Document chineseMedicines = null;
        Document westernMedicines = null;
        try {
         medicines = (Document) ehealrecordDocument.get("medicine");
            chineseMedicines = (Document) medicines.get("chineseMedicines");
            westernMedicines = (Document) medicines.get("westernMedicines");
        } catch (Exception e) {
        	// TODO: handle exception
        	// System.out.println();
        }
        
        //诊断doc
        Document diagnostics = (Document) ehealrecordDocument.get("diagnostics");
        String chinesediagnostics = diagnostics.getString("chinesediagnostics");
        String westerndiagnostics = diagnostics.getString("westerndiagnostics");
        
        List<Document> wMedicineList = null;
        Document wMedicine = null;
        
        if(westernMedicines != null){
        	if(westernMedicines.get("westernMedicine") instanceof ArrayList){
            	wMedicineList = (List<Document>) westernMedicines.get("westernMedicine");
            }else{
            	wMedicine = (Document) westernMedicines.get("westernMedicine");
            }
        }
        
        @SuppressWarnings("unchecked")
        List<Document> cMedicineList = null;
        Document cMedicine = null;
        
        if(chineseMedicines != null){
        	if(chineseMedicines.get("chineseMedicine") instanceof ArrayList){
        		cMedicineList = (List<Document>) chineseMedicines.get("chineseMedicine");
        	}else{
        		cMedicine = (Document) chineseMedicines.get("chineseMedicine");
        	}
        }
        
		
        
        List<ChineseMedicine> chineseMedicinesList = null;// 中药处方
        List<WesternMedicine> westernMedicinesList = null; // 西药处方
        PatientInfo patientInfo = null; //病人信息
        //中药处方
        if(chineseMedicines != null){
        	//多种中药
            if(cMedicineList != null && cMedicineList.size() > 0){
        	
            	chineseMedicinesList = new ArrayList<ChineseMedicine>();
        	
            	for(Document doc : cMedicineList){
            			ChineseMedicine c = listToChineseMedicine(doc);
            			if(c != null){
            				chineseMedicinesList.add(c);
            			}
            	}
            }
            // 只有一种中药
            if(cMedicine != null){
            	chineseMedicinesList = new ArrayList<ChineseMedicine>();
            	ChineseMedicine c = listToChineseMedicine(chineseMedicines);
            	if(c != null){
            		chineseMedicinesList.add(c);
            	}
            }
        }
        //西药处方
        if(wMedicineList != null || wMedicine != null){
        	//有西药
        	westernMedicinesList = new ArrayList<WesternMedicine>();
        	if(wMedicine != null){
        		//只有一种西药
        		WesternMedicine westernMedicine = docToWerstenMedicine(wMedicine);
        		if(westernMedicine != null){
                            westernMedicinesList.add(westernMedicine);
                        }
                       
        	}
        	if(wMedicineList != null && wMedicineList.size() > 0){
        		// 多种西药
        		for(Document d : wMedicineList){
        			WesternMedicine w = docToWerstenMedicine(d);
        			if(w != null){
                                    westernMedicinesList.add(w);
                                }
                               
        		}
        	}
        }
        
        //病人信息
        patientInfo = docToPatientInfo(patientInfoDoc);       
        String batchString = String.valueOf(ehealrecordDocument.get("batch"));        
        String dateString = ehealrecordDocument.getString("date"); // 日期
        String doctorString = ehealrecordDocument.getString("doctor"); // 医师
        String medicineservice = ehealrecordDocument.getString("medicineservice"); // 科室
        String process = ehealrecordDocument.getString("process"); // 处理
        String registrationno = String.valueOf(ehealrecordDocument.get("registrationno")); //  挂号号
        String hospitalString = ehealrecordDocument.getString("hospital");// 医院
        String conditionsdescribed = ehealrecordDocument.getString("conditionsdescribed"); // 病症描述
                
        EHealthRecord eHealthRecord = new EHealthRecord();
        eHealthRecord.setBatchString(batchString);
        eHealthRecord.setId(_id.toString());
        eHealthRecord.setDate(dateString);
        eHealthRecord.setDoctor(doctorString);
        eHealthRecord.setMedicalService(medicineservice);
        eHealthRecord.setProcessString(process);
        eHealthRecord.setRegistrationno(registrationno);
        eHealthRecord.setHospital(hospitalString);
        eHealthRecord.setConditionsdescribed(conditionsdescribed);
        eHealthRecord.setChinesediagnostics(chinesediagnostics);
        eHealthRecord.setWesterndiagnostics(westerndiagnostics);
        
        //病人信息
        eHealthRecord.setPatientInfo(patientInfo);
        // 中医处方
        if(chineseMedicinesList != null && chineseMedicinesList.size() > 0){
        	eHealthRecord.setChineseMedicines(chineseMedicinesList);
//                System.out.println("-----------add chinese success");
        }
        // 西医处方
        if(westernMedicinesList != null && westernMedicinesList.size() > 0){
        	eHealthRecord.setWesternMedicines(westernMedicinesList);
        }
        //诊断信息
        
        return eHealthRecord;
    }
    
    /**
     * 
     * @param doc
     * @return
     */
    public static ChineseMedicine listToChineseMedicine(Document doc){
    	if(doc == null){
    		return null;
    	}
    	
    	String nameString = "";
    	String number = "";
    	String uniString = "";
    	List<String> biasList = new ArrayList<String>();
    	
    	nameString = doc.getString("cname");
        //数字bug：字符/数字都要，区别对待
    	try{
            number = String.valueOf(doc.getDouble("number")) ;
        }catch(Exception x){
            number = doc.getString("number");
        }
    	uniString = doc.getString("unit");
    	
    	//别名
    	for(int i = 1; i < 4;i++){
    		String bString = doc.getString("bias"+i);
    		if(bString == null){
    			break;
    		}
    		biasList.add(bString);
    	}    	
    	
    	ChineseMedicine chineseMedicine = new ChineseMedicine(nameString, biasList, number, uniString);
    	return chineseMedicine;
    	
    }
    
    /**
     *  doc----> westernmedicine
     * @param doc
     * @return
     */
    public static WesternMedicine docToWerstenMedicine(Document doc){
    	if(doc == null){
    		return null;
    	}
                
//        String group = String.valueOf(doc.getDouble("group"));
    	String group = "";
    	String wname = doc.getString("wname");;
    	String amount = doc.getString("amount");
    	String usage = doc.getString("usage");
    	String specifications = doc.getString("specifications");
    	

        WesternMedicine westernMedicine = new WesternMedicine();
        westernMedicine.setGroupString(group);
        westernMedicine.setNameString(wname);
        westernMedicine.setAmountString(amount);
        westernMedicine.setUsageString(usage);
        westernMedicine.setSpecifications(specifications);
        
    	
    	return westernMedicine;
    }
    
    /**
     * Document{{profession=, address=海南省琼机运公, gender=男, phone=, contact=, name=杨务林, age=68岁}
     * @param doc
     * @return
     */
    public static PatientInfo docToPatientInfo(Document doc){
    	if(doc == null){
    		return null;
    	}
    	PatientInfo patientInfo = new PatientInfo();
    	
    	String name = doc.getString("name");
    	String gender = doc.getString("gender");
    	String age = doc.getString("age");
    	String profession = doc.getString("profession");
    	String address = doc.getString("address");
//    	String phone = String.valueOf(doc.getLong("phone"));
//        String phone = doc.getString("phone");
    	String contact = doc.getString("contact");
    	
    	patientInfo.setName(name);
    	patientInfo.setGender(gender);
    	patientInfo.setAddress(address);
    	patientInfo.setAge(age);
    	patientInfo.setProfession(profession);
//    	patientInfo.setPhoneNumber(phone);
    	patientInfo.setContact(contact);
    	
    	return patientInfo;
    }
    
    /**
     * 
     * @param ags
     * @return 
     */
    public static int main(String[] ags){
        
        
        MongoClient client = new MongoClient("localhost",27017);
		
        if(client != null){
            System.out.println("success");
	}else{
            System.out.println("failed");
	}
		
        MongoDatabase database = client.getDatabase("db");
		
	if( database == null){
            System.out.println("db is null");
	}else {
            System.out.println("db is not null");
	}
		
	MongoCollection<Document> collection = database.getCollection("ehealth");
//		System.out.println(collection.count());
		

		
	MongoCursor<Document> cursor = collection.find(new BasicDBObject("ehealthrecord.registrationno","600025873102")).iterator();
		
	while(cursor.hasNext()){
            System.out.println(cursor.next());
	}
        
        
        return 0;
    }
    
}
