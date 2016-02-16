package com.um.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.um.dao.ConnectionDB;
import com.um.model.EHealthRecord;
import com.um.mongodb.converter.EhealthRecordConverter;
import com.um.util.MedicineByDescription;

public class StandardDescription {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// 1. load the data of batch
		List<EHealthRecord> eHealthRecords = new ArrayList<EHealthRecord>() ; // All records data
		
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
				eHealthRecords.add(eHealthRecord);
			}
			
		});
		
		System.out.println("all records size: " + eHealthRecords.size());
		
		// 2. get all description of records
		// check the empty description of records
		System.out.println("---------description empty-----------");
		for (EHealthRecord eHealthRecord : eHealthRecords) {
			if (eHealthRecord.getConditionsdescribed().equals("")) {
				System.out.println(eHealthRecord.getRegistrationno());
			}
		}
		System.out.println("-------------------------------------");
		// 3. standard the description of records
		String descriptionString = MedicineByDescription.formattedDescriptionByCount(eHealthRecords.get(0).getConditionsdescribed());
		System.out.println(eHealthRecords.get(0).getConditionsdescribed() + " | " + descriptionString);
		
		System.exit(0);
		
		// 4. save the standard description
		File statText = new File("/Users/heermaster/Documents/file/stantarddescription.txt");
        FileOutputStream is = new FileOutputStream(statText);
        OutputStreamWriter osw = new OutputStreamWriter(is);    
        Writer w = new BufferedWriter(osw);
        for (EHealthRecord eRecord: eHealthRecords) {
			String outString = eRecord.getConditionsdescribed() + "-----" + MedicineByDescription.formattedDescriptionByCount(eRecord.getConditionsdescribed());
			w.write(outString + "\n");
        }
        w.close();
        System.out.println("-----Process end-------");
	}

}
