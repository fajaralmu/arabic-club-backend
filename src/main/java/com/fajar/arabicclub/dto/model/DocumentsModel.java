package com.fajar.arabicclub.dto.model;

import com.fajar.arabicclub.annotation.Dto;
import com.fajar.arabicclub.annotation.FormField;
import com.fajar.arabicclub.constants.FieldType;
import com.fajar.arabicclub.constants.FormInputColumn;
import com.fajar.arabicclub.entity.Documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto//(entityClass=Documents.class)//, updateService =  "documentUpdateService")
 
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentsModel extends BaseModel<Documents> {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = -3157890799698948710L;
 
	@FormField
	private String title;
	 
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA, required = false)
	private String description;
	 
	@FormField(optionItemName = "name", type = FieldType.FIELD_TYPE_DYNAMIC_LIST)
	private DocumentCategoryModel category;  
	@FormField
	private String fileName;

}
