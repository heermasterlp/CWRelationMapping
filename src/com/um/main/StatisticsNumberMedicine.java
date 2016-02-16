package com.um.main;

import java.text.DecimalFormat;
import java.util.List;

import com.um.classify.CWRelationMapping;
import com.um.model.EHealthRecord;

public class StatisticsNumberMedicine {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		CWRelationMapping cwRelationMapping = new CWRelationMapping();
		List<EHealthRecord> eHealthRecords = cwRelationMapping.queryEhealthData();
		int length = eHealthRecords.size(); // 长度
		int count = 0;
		
		DecimalFormat df = new DecimalFormat("0.00");
		// 病例中中药处方个数 ＝ 13
		for(EHealthRecord e : eHealthRecords){
			if(e.getChineseMedicines() != null && e.getChineseMedicines().size() == 13){
//				System.out.println("size is 13");
				count++;
			}
		}
		
		System.out.println("medicine size is 13 : " + count + " percent" + df.format(100.0 *count /length ) + "%");
		
		count = 0;
		//中药处方个数 ＝ 14
		for(EHealthRecord e : eHealthRecords){
			if(e.getChineseMedicines() != null && e.getChineseMedicines().size() == 14){
//				System.out.println("size is 13");
				count++;
			}
		}
		System.out.println("medicine size is 14 : " + count + " percent" + df.format(100.0 *count /length ) + "%");
		

		count = 0;
		//中药处方个数 ＝ 15
		for(EHealthRecord e : eHealthRecords){
			if(e.getChineseMedicines() != null && e.getChineseMedicines().size() == 15){
//				System.out.println("size is 13");
				count++;
			}
		}
		System.out.println("medicine size is 15 : " + count + " percent" + df.format(100.0 *count /length ) + "%");
		
		count = 0;
		//中药处方个数 ＝ 16
		for(EHealthRecord e : eHealthRecords){
			if(e.getChineseMedicines() != null && e.getChineseMedicines().size() == 16){
//				System.out.println("size is 13");
				count++;
			}
		}
		System.out.println("medicine size is 16 : " + count + " percent" + df.format(100.0 *count /length ) + "%");
		
		count = 0;
		//中药处方个数的平均值
		for(EHealthRecord e : eHealthRecords){
			if(e.getChineseMedicines() != null){
//				System.out.println("size is 13");
				count += e.getChineseMedicines().size();
			}
		}
		System.out.println("the medicine average :" + (count / length));
		
	}

}
