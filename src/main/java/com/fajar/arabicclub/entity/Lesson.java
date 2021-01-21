package com.fajar.arabicclub.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fajar.arabicclub.annotation.Dto;
import com.fajar.arabicclub.annotation.FormField;
import com.fajar.arabicclub.constants.FieldType;
import com.fajar.arabicclub.constants.FormInputColumn;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto(formInputColumn = FormInputColumn.ONE_COLUMN, updateService =  "lessonUpdateService")
@Entity
@Table(name = "lesson")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Lesson extends BaseEntity {

	/**
	* 
	*/
	private static final long serialVersionUID = 3494963248002164943L;
	@Column(unique = true)
	@FormField
	private String title;
	@Column(length = 2000)
	@FormField(type = FieldType.FIELD_TYPE_TEXTEDITOR)
	private String content;
	@Column
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	private String description;
	
	@Column(name = "banner_images" )
	@FormField(type = FieldType.FIELD_TYPE_IMAGE, required = false, multipleImage = true, defaultValue = "Default.BMP")
	private String bannerImages; // type:BLOB
	
	@JoinColumn(name = "category_id", nullable = false)
	@ManyToOne
	@FormField(optionItemName = "name", type = FieldType.FIELD_TYPE_DYNAMIC_LIST)
	private LessonCategory category;

	@JoinColumn(name = "user_id", nullable = false)
	@ManyToOne
	private User user;


}
