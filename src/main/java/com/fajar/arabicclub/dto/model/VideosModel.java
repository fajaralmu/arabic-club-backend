package com.fajar.arabicclub.dto.model;

import com.fajar.arabicclub.annotation.Dto;
import com.fajar.arabicclub.annotation.FormField;
import com.fajar.arabicclub.constants.FieldType;
import com.fajar.arabicclub.dto.youtuberesponse.Snippet;
import com.fajar.arabicclub.entity.Videos;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto(entityClass=Videos.class) 
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideosModel extends BaseModel<Videos> {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = -5237543073310094900L;
 
	@FormField
	private String title;
	 
	@FormField(required = true)
	private String url; 
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	private String description;
	 
	@FormField(optionItemName = "name", type = FieldType.FIELD_TYPE_FIXED_LIST)
	private VideoCategoryModel category; 
	
	
	private Snippet videoSnippet;
 

}
