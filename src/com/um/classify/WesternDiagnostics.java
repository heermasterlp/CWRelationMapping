package com.um.classify;

import java.util.ArrayList;
import java.util.List;

import com.um.model.EHealthRecord;

public class WesternDiagnostics {
	
	/**
	 * 西医诊断分类：
	 * 		根据不同的西医诊断（关键字）进行分类
	 * @param args
	 */
	/*
	 *  分类描述
	 */
	private String westernDiagString;
	
	/*
	 *  关键字
	 */
	private String[] keywords;
	
	/*
	 * 病历统计
	 */
	private List<EHealthRecord> eHealthRecords;
	
	
	public WesternDiagnostics(){
		
		westernDiagString = "";		
		keywords = null;		
		eHealthRecords = new ArrayList<EHealthRecord>();
	}
	
	
	public String toString(){
		return "类名：" + westernDiagString;
	}
	

	public String getWesternDiagString() {
		return westernDiagString;
	}


	public void setWesternDiagString(String westernDiagString) {
		this.westernDiagString = westernDiagString;
	}


	public String[] getKeywords() {
		return keywords;
	}


	public void setKeywords(String[] keywords) {
		this.keywords = keywords;
	}


	public List<EHealthRecord> geteHealthRecords() {
		return eHealthRecords;
	}


	public void seteHealthRecords(List<EHealthRecord> eHealthRecords) {
		this.eHealthRecords = eHealthRecords;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
