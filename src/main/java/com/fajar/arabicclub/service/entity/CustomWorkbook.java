package com.fajar.arabicclub.service.entity;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CustomWorkbook extends XSSFWorkbook{
	
	private String fileName;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	

}
