package com.fajar.arabicclub.dto.model;

import com.fajar.arabicclub.annotation.Dto;
import com.fajar.arabicclub.annotation.FormField;
import com.fajar.arabicclub.constants.FieldType;
import com.fajar.arabicclub.entity.VideoCategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Dto("Video Category") 
@Data
@Builder	
@AllArgsConstructor
@NoArgsConstructor
public class VideoCategoryModel extends BaseModel<VideoCategory> {  
	/**
	 * 
	 */
	private static final long serialVersionUID = -8604334009301906788L;
	@FormField  
	private String code;
	@FormField  
	private String name;
	@FormField ( type= FieldType.FIELD_TYPE_TEXTAREA)  
	private String description;
}
