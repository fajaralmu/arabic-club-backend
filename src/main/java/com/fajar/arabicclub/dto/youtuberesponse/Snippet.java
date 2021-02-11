package com.fajar.arabicclub.dto.youtuberesponse;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Snippet implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = -6507411031595709246L;
	private String publishedAt, channelId, title, description;
	private Thumbnails thumbnails;
	private String channelTitle;
	private String categoryId;
	private String liveBroadcastContent;
	private String defaultAudioLanguage;
	
	@Data
	@NoArgsConstructor
	public static class Localized implements Serializable {/**
		 * 
		 */
		private static final long serialVersionUID = -3718020354968657691L;
		private String title, description; 
		
	}
}
