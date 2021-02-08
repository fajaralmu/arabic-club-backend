package com.fajar.arabicclub.dto.model;

import com.fajar.arabicclub.annotation.Dto;
import com.fajar.arabicclub.annotation.FormField;
import com.fajar.arabicclub.constants.FieldType;
import com.fajar.arabicclub.entity.ImageCategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Dto(value="Image Category", entityClass=ImageCategory.class) 
@Data
@Builder	
@AllArgsConstructor
@NoArgsConstructor
public class ImageCategoryModel extends BaseModel<ImageCategory> { 
	/**
	 * 
	 */
	private static final long serialVersionUID = -6022788710426434739L;
	@FormField  
	private String code;
	@FormField  
	private String name;
	@FormField  
	private String iconClassName;
	@FormField ( type= FieldType.FIELD_TYPE_TEXTAREA)  
	private String description;
}
