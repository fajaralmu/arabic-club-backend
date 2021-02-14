package com.fajar.arabicclub.dto.model;

import com.fajar.arabicclub.annotation.Dto;
import com.fajar.arabicclub.annotation.FormField;
import com.fajar.arabicclub.constants.FieldType;
import com.fajar.arabicclub.entity.DocumentCategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Dto(value="Document Category", entityClass=DocumentCategory.class) 
@Data
@Builder	
@AllArgsConstructor
@NoArgsConstructor
public class DocumentCategoryModel extends BaseModel<DocumentCategory> { 
	 
	  
	/**
	 * 
	 */
	private static final long serialVersionUID = 531597584236464568L;
	@FormField  
	private String code;
	@FormField  
	private String name;
	@FormField (required = false) 
	private String iconClassName;
	@FormField ( type= FieldType.FIELD_TYPE_TEXTAREA, required = false)  
	private String description;
}
