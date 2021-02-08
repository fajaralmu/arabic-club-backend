package com.fajar.arabicclub.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fajar.arabicclub.dto.model.ImagesModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Entity
@Table(name = "images")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Images extends BaseEntity<ImagesModel> implements MultipleImageModel {

	/**
	* 
	*/
	private static final long serialVersionUID = 3494963248002164943L;
	@Column(unique = true) 
	private String title;
	
	@Column 
	private String description;
	
	@JoinColumn(name = "category_id", nullable = false)
	@ManyToOne 
	private ImageCategory category; 
 
	@Column(name = "images" , nullable = false) 
	private String images; // type:BLOB
	
	@Override
	public void setImageNames(String[] imageNames) {
		setImages(String.join("~", imageNames));
		
	}

	@Override
	public String[] getImageNames() {
		if (null == this.images) 
		{
			return new String[] {};
		}
		return this.images.split("~");
	}

}
