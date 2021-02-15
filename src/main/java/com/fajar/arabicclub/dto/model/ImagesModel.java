package com.fajar.arabicclub.dto.model;

import com.fajar.arabicclub.annotation.Dto;
import com.fajar.arabicclub.annotation.FormField;
import com.fajar.arabicclub.constants.FieldType;
import com.fajar.arabicclub.entity.Images;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto(entityClass=Images.class, updateService =  "imagesUpdateService")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImagesModel extends  BaseModel<Images> { 
  

	/**
	 * 
	 */
	private static final long serialVersionUID = 2498892758398614324L;
 
	@FormField
	private String title; 
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA, required = false,)
	private String description; 
	@FormField(optionItemName = "name", type = FieldType.FIELD_TYPE_FIXED_LIST)
	private ImageCategoryModel category;  
	@FormField(type = FieldType.FIELD_TYPE_IMAGE, required = true, multipleImage = true, defaultValue = "Default.BMP")
	private String images; // type:BLOB
	
	

}
