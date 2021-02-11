package com.fajar.arabicclub.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fajar.arabicclub.dto.model.LessonModel;
import com.fajar.arabicclub.entity.setting.MultipleImageModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Entity
@Table(name = "lesson")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Lesson extends BaseEntity<LessonModel> implements MultipleImageModel {

	/**
	* 
	*/
	private static final long serialVersionUID = 3494963248002164943L;
	@Column( ) 
	private String title;
	@Column(length = 20000) 
	private String content;
	@Column 
	private String description;
	@Column
	private Date date;
	
	@Column(name = "banner_images" ) 
	private String bannerImages; // type:BLOB
	
	@JoinColumn(name = "category_id", nullable = false)
	@ManyToOne 
	private LessonCategory category;

	@JoinColumn(name = "user_id", nullable = false)
	@ManyToOne
	private User user;

	@Override
	public void setImageNames(String[] imageNames) {
		setBannerImages(String.join("~", imageNames));
		
	}

	@Override
	public String[] getImageNames() {
		if (null == this.bannerImages) 
		{
			return new String[] {};
		}
		return this.bannerImages.split("~");
	}


}
