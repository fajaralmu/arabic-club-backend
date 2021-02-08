package com.fajar.arabicclub.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fajar.arabicclub.annotation.Dto;
import com.fajar.arabicclub.dto.model.VideoCategoryModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "video_category")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoCategory extends BaseEntity<VideoCategoryModel> {
	/**
	* 
	*/
	private static final long serialVersionUID = -1168912843978053906L;
	@Column(unique = true)
	private String code;
	@Column(unique = true)
	private String name;
	@Column
	private String description;
}
