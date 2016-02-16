package com.um.classify;

import java.util.ArrayList;
import java.util.List;

import com.um.model.EHealthRecord;

public class ChineseDiagnostics {
	
	/**
	 *  中医诊断分类： 根据不同的中医诊断（关键字）进行分类
	 *  	
	 * @param args
	 */
	
	/*
	 *  分类描述，例如：肺癌气虚痰瘀互结
	 */
	private String chineseDiagString;
	
	private String chineseDiagCode;
	
	public String getChineseDiagCode() {
		return chineseDiagCode;
	}

	public void setChineseDiagCode(String chineseDiagCode) {
		this.chineseDiagCode = chineseDiagCode;
	}


	/*
	 *  关键字
	 */
	private String[] keywrods;
	
	/*
	 *  病历统计
	 */
	private List<EHealthRecord> eHealthRecords;
	
	
	public ChineseDiagnostics(){
		
		chineseDiagString = "";
		keywrods = null;
		eHealthRecords = new ArrayList<EHealthRecord>();
	}
	
	public String toString(){
		
		return "类名：" + chineseDiagString;
	}
	
	
	public String getChineseDiagString() {
		return chineseDiagString;
	}


	public void setChineseDiagString(String chineseDiagString) {
		this.chineseDiagString = chineseDiagString;
	}


	public String[] getKeywrods() {
		return keywrods;
	}


	public void setKeywrods(String[] keywrods) {
		this.keywrods = keywrods;
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
