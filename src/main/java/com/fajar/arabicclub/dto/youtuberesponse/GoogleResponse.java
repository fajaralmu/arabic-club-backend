package com.fajar.arabicclub.dto.youtuberesponse;

import java.io.Serializable;
import java.util.List;

import lombok.NoArgsConstructor;

import lombok.Data;
@Data
@NoArgsConstructor
public class GoogleResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2703398959456901508L;
	private String kind, etag;
	private List<ResponseItem> items;
	
	@Data
	@NoArgsConstructor
	public static class PageInfo implements Serializable {/**
		 * 
		 */
		private static final long serialVersionUID = -1447076194759439200L;
		private String totalResults;
		private String resultsPerPage;
		
	}

}
