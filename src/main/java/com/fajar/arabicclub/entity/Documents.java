package com.fajar.arabicclub.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fajar.arabicclub.annotation.Dto;
import com.fajar.arabicclub.constants.FormInputColumn;
import com.fajar.arabicclub.dto.model.DocumentsModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto(formInputColumn = FormInputColumn.ONE_COLUMN)//, updateService =  "documentUpdateService")
@Entity
@Table(name = "documents")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Documents extends BaseEntity<DocumentsModel> {

	/**
	* 
	*/
	private static final long serialVersionUID = 3494963248002164943L;
	@Column(unique = true) 
	private String title;
	
	@Column 
	private String description;
	
	@JoinColumn(name = "category_id", nullable = false)
	@ManyToOne 
	private DocumentCategory category; 
	@Column 
	private String fileName;

}
