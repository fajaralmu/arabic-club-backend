package com.fajar.arabicclub.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fajar.arabicclub.dto.model.VideosModel;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "videos")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Videos extends BaseEntity<VideosModel> {

	/**
	* 
	*/
	private static final long serialVersionUID = 3494963248002164943L;
	@Column(unique = true)
	private String title;

	@Column(nullable = false)
	private String url;
	@Column
	private String description;

	@JoinColumn(name = "category_id", nullable = false)
	@ManyToOne
	private VideoCategory category;
	@JsonIgnore
	public String getVideoId() {
		if (null == url)
			return null;
		try {

			return url.split("\\?v=")[1];
		} catch (Exception e) {
//			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		Videos v = Videos.builder().url("https://www.youtube.com/watch?v=AZUJca__mwY").build();
		System.out.println(v.getVideoId());
	}
	

}
