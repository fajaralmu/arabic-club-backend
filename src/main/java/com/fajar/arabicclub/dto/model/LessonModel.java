package com.fajar.arabicclub.dto.model;

import java.util.Date;

import com.fajar.arabicclub.annotation.Dto;
import com.fajar.arabicclub.annotation.FormField;
import com.fajar.arabicclub.constants.FieldType;
import com.fajar.arabicclub.entity.Lesson;
import com.fajar.arabicclub.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto( updateService =  "lessonUpdateService") 
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LessonModel extends BaseModel<Lesson>{
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 6770433671659733773L;
	@FormField
	private String title; 
	@FormField(type = FieldType.FIELD_TYPE_TEXTEDITOR)
	private String content; 
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA, required = false)
	private String description;
	 
	@FormField(type = FieldType.FIELD_TYPE_IMAGE, required = false, multipleImage = true, defaultValue = "Default.BMP")
	private String bannerImages; // type:BLOB
	 
	@FormField(optionItemName = "name", type = FieldType.FIELD_TYPE_FIXED_LIST)
	private LessonCategoryModel category;
	
	@FormField(type = FieldType.FIELD_TYPE_DATETIME)
	private Date date;
	@FormField(editable = false, optionItemName = "name")
	private UserModel user;

 

}
