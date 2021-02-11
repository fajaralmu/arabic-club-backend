package com.fajar.arabicclub.service.publicmenus;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fajar.arabicclub.dto.model.VideosModel;
import com.fajar.arabicclub.dto.youtuberesponse.GoogleResponse;
import com.fajar.arabicclub.dto.youtuberesponse.ResponseItem;
import com.fajar.arabicclub.dto.youtuberesponse.Snippet;
import com.fajar.arabicclub.entity.Videos;
import com.fajar.arabicclub.service.ProgressService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class YoutubeVideoPreviewService {

	@Value("${app.google.api.key}")
	private String apiKey;
	@Value("${app.google.api.video.url}")
	private String apiUrl;
	
	@Autowired
	private ProgressService progressSerivce;


	static RestTemplate restTemplate = new RestTemplate();
	static String url = "https://www.googleapis.com/youtube/v3/videos", key = "AIzaSyBbV0mo3GHjfkHRz90L9FmfPwH_M_mijxE",
			id = "0ehjOd6YD5M";

	public static void main(String[] args) {
		String endpoint = buildEndpoint(url, key, id);
		ResponseEntity<GoogleResponse> response = restTemplate.getForEntity(endpoint, GoogleResponse.class);
		System.out.println(response.getBody());
	}

	private static String buildEndpoint(String url2, String key2, String id2) {
		return url2 + "?key=" + key + "&part=snippet&id=" + id2;
	}

	public Snippet getVideoSnippet(Videos video) {
		String id = video.getVideoId();
		if (null == id) {
			return null;
		}
		String endpoint = buildEndpoint(apiUrl, apiKey, id);
		try {
			ResponseEntity<GoogleResponse> response = restTemplate.getForEntity(endpoint, GoogleResponse.class);
			List<ResponseItem> items = response.getBody().getItems();
			if (null == items || items.size() == 0) {
				log.info("items empty");
				return null;
			}
		
		return items.get(0).getSnippet();
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	public List<VideosModel> getSnippets(List<Videos> videos, HttpServletRequest httpServletRequest) {
		log.info("GET video snippets");
		List<VideosModel> models = new ArrayList<VideosModel>();
		for (Videos video : videos) {
			Snippet snippet = getVideoSnippet(video);
			VideosModel model = video.toModel();
			model.setVideoSnippet(snippet);
			models.add(model);
			progressSerivce.sendProgress(1, videos.size(), 80, httpServletRequest);
		}
		return models ;
	}

}
