package com.fajar.arabicclub.dto;

import java.io.Serializable;
import java.util.List;

import com.fajar.arabicclub.annotation.Dto;
import com.fajar.arabicclub.entity.ApplicationProfile;
import com.fajar.arabicclub.entity.BaseEntity;
import com.fajar.arabicclub.entity.Category;
import com.fajar.arabicclub.entity.RegisteredRequest;
import com.fajar.arabicclub.entity.User;

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
	private Category category;
	private RegisteredRequest registeredrequest;  

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
