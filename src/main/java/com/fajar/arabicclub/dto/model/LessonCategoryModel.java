package com.fajar.arabicclub.dto.model;

import com.fajar.arabicclub.annotation.Dto;
import com.fajar.arabicclub.annotation.FormField;
import com.fajar.arabicclub.constants.FieldType;
import com.fajar.arabicclub.entity.LessonCategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Dto(value="Lesson Category", entityClass=LessonCategory.class) 
@Data
@Builder	
@AllArgsConstructor
@NoArgsConstructor
public class LessonCategoryModel extends BaseModel<LessonCategory> { 
	/**
	 * 
	 */
	private static final long serialVersionUID = 2119316402415666731L;
	@FormField
	private String code; 
	@FormField
	private String name; 
	@FormField
	private String iconClassName; 
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	private String description;
}
