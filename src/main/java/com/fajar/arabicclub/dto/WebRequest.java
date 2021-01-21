package com.fajar.arabicclub.dto;

import java.io.Serializable;
import java.util.List;

import com.fajar.arabicclub.annotation.Dto;
import com.fajar.arabicclub.entity.ApplicationProfile;
import com.fajar.arabicclub.entity.BaseEntity;
import com.fajar.arabicclub.entity.DocumentCategory;
import com.fajar.arabicclub.entity.Documents;
import com.fajar.arabicclub.entity.ImageCategory;
import com.fajar.arabicclub.entity.Images;
import com.fajar.arabicclub.entity.Lesson;
import com.fajar.arabicclub.entity.LessonCategory;
import com.fajar.arabicclub.entity.User;
import com.fajar.arabicclub.entity.VideoCategory;
import com.fajar.arabicclub.entity.Videos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 110411933791444017L;


	
	/**
	 * ENTITY CRUD use lowerCase!!!
	 */

	private String entity;
	private User user; 
	private ApplicationProfile profile; 
	private Lesson lesson;
	private LessonCategory lessoncategory;
	
	private Videos videos;
	private VideoCategory videocategory;
	
	private Images images;
	private ImageCategory imagecategory;
	
	private Documents documents;
	private DocumentCategory documentcategory;
	

	/**
	 * ==========end entity============
	 */

	private Filter filter; 
	
	private BaseEntity entityObject;
	private AttachmentInfo attachmentInfo; 
	private List<BaseEntity> orderedEntities; 
	
	private boolean regularTransaction;
	
	private String imageData; 

}
