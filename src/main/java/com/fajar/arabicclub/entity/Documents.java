package com.fajar.arabicclub.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fajar.arabicclub.dto.AttachmentInfo;
import com.fajar.arabicclub.dto.model.DocumentsModel;
import com.fajar.arabicclub.entity.setting.SingleDocumentModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Entity
@Table(name = "documents")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Documents extends BaseEntity<DocumentsModel> implements SingleDocumentModel{

	/**
	* 
	*/
	private static final long serialVersionUID = 3494963248002164943L;
	@Column( ) 
	private String title;
	
	@Column 
	private String description;
	
	@JoinColumn(name = "category_id", nullable = false)
	@ManyToOne 
	private DocumentCategory category; 
	@Column 
	private String fileName;
	@Transient
	private AttachmentInfo attachmentInfo;
	@Override
	public void setDocumentName(String doc) {
		this.fileName = doc;
		
	}
	@Override
	public String getDocumentName() { 
		return fileName;
	}
	

}
