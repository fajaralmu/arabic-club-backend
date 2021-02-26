package com.fajar.arabicclub.entity.setting;

import com.fajar.arabicclub.dto.AttachmentInfo;

public interface SingleDocumentModel {
	
	Long getId();
	public void setDocumentName(String doc);
	String getDocumentName();
	AttachmentInfo getAttachmentInfo();

}
