package com.fajar.arabicclub.service.resources;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileRemovalService {
	@Value("${app.resources.file.api.remove.image}")
	private String apiRemoveImageEndpoint;
	@Value("${app.resources.file.api.remove.document}")
	private String apiRemoveDocumentEndpoint;
	RestTemplate restTemplate = new RestTemplate();

	public boolean removeImage(String imageName) {
		return removeViaApi(imageName, apiRemoveImageEndpoint);

	}

	private boolean removeViaApi(String fileName, String URL) {

		log.info("REMOVE file: {} VIA API TO : {}",fileName, URL);
		LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		String response = null;

		try {
			map.add("name", fileName);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
			ResponseEntity<String> responseEntity = restTemplate.postForEntity(URL, requestEntity, String.class);
			log.info("remove image code: {}", responseEntity.getStatusCode());
			response = responseEntity.getBody();
			return true;
		} catch (HttpStatusCodeException e) {
			e.printStackTrace();
			response = e.getResponseBodyAsString();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			response = e.getMessage();
			return false;
		} finally {
			log.info("remove FILE response: {}", response);
		}
	}

	public boolean removeDocument(String fileName) {
		return removeViaApi(fileName, apiRemoveDocumentEndpoint);

	}

}
