package com.fajar.arabicclub.dto;

import java.io.Serializable;
import java.util.List;

import com.fajar.arabicclub.annotation.Dto;
import com.fajar.arabicclub.dto.model.ApplicationProfileModel;
import com.fajar.arabicclub.dto.model.DocumentCategoryModel;
import com.fajar.arabicclub.dto.model.DocumentsModel;
import com.fajar.arabicclub.dto.model.ImageCategoryModel;
import com.fajar.arabicclub.dto.model.ImagesModel;
import com.fajar.arabicclub.dto.model.LessonCategoryModel;
import com.fajar.arabicclub.dto.model.LessonModel;
import com.fajar.arabicclub.dto.model.QuizModel;
import com.fajar.arabicclub.dto.model.UserModel;
import com.fajar.arabicclub.dto.model.VideoCategoryModel;
import com.fajar.arabicclub.dto.model.VideosModel;
import com.fajar.arabicclub.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

 
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
	private UserModel user; 
	private ApplicationProfileModel profile; 
	private LessonModel lesson;
	private LessonCategoryModel lessoncategory;
	
	private VideosModel videos;
	private VideoCategoryModel videocategory;
	
	private ImagesModel images;
	private ImageCategoryModel imagecategory;
	
	private DocumentsModel documents;
	private DocumentCategoryModel documentcategory;
	
	private QuizModel quiz;
	

	/**
	 * ==========end entity============
	 */

	private Filter filter; 
	
	private BaseEntity entityObject;
	private AttachmentInfo attachmentInfo; 
	private List<BaseEntity> orderedEntities; 
	
	private boolean regularTransaction;
	
	private String imageData; 
	private String requestId;
	private String token;

}
