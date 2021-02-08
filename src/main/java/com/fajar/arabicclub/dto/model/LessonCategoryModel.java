package com.fajar.arabicclub.dto.model;

import javax.persistence.Column;

import com.fajar.arabicclub.annotation.Dto;
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
	@Column(unique = true)
	private String code; 
	@Column(unique = true)
	private String name; 
	@Column(name = "icon_class")
	private String iconClassName; 
	@Column
	private String description;
}
