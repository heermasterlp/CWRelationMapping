package com.um.main;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.um.classify.CWRelationMapping;
import com.um.model.EHealthRecord;

public class RequestName {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CWRelationMapping cwRelationMapping = new CWRelationMapping();
		List<EHealthRecord> aEHealthRecords = cwRelationMapping.queryEhealthData();
		Set<String> patientNameSet = new HashSet<String>();
		for(EHealthRecord e:aEHealthRecords){
			if(e.getPatientInfo() != null){
				String nameString = e.getPatientInfo().getName();
				patientNameSet.add(nameString);
			}
		}
		
		System.out.println(patientNameSet);
	}

}
