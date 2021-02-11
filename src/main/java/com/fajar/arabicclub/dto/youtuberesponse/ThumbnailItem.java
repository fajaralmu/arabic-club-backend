package com.fajar.arabicclub.dto.youtuberesponse;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ThumbnailItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1247763136232311388L;
	private String url;
	private int height, width;
}
