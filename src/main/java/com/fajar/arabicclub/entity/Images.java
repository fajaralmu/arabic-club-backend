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

@Dto(formInputColumn = FormInputColumn.ONE_COLUMN, updateService =  "imagesUpdateService")
@Entity
@Table(name = "images")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Images extends BaseEntity implements MultipleImageModel {

	/**
	* 
	*/
	private static final long serialVersionUID = 3494963248002164943L;
	@Column(unique = true)
	@FormField
	private String title;
	
	@Column
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	private String description;
	
	@JoinColumn(name = "category_id", nullable = false)
	@ManyToOne
	@FormField(optionItemName = "name", type = FieldType.FIELD_TYPE_DYNAMIC_LIST)
	private ImageCategory category; 
 
	@Column(name = "images" , nullable = false)
	@FormField(type = FieldType.FIELD_TYPE_IMAGE, required = true, multipleImage = true, defaultValue = "Default.BMP")
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
