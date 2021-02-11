package com.fajar.arabicclub.dto.youtuberesponse;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Thumbnails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2862691312981117176L;
	@JsonProperty("default")
	private ThumbnailItem defaultProperty;
	private ThumbnailItem medium, high, standard;
}
