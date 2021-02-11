package com.fajar.arabicclub.dto.youtuberesponse;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5416589177353392106L;
	private String kind, etag, id;
	private Snippet snippet;

}
