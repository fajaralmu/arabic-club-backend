package com.fajar.arabicclub.service.publicmenus;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fajar.arabicclub.dto.WebRequest;
import com.fajar.arabicclub.dto.WebResponse;
import com.fajar.arabicclub.dto.model.VideosModel;
import com.fajar.arabicclub.entity.Documents;
import com.fajar.arabicclub.entity.Images;
import com.fajar.arabicclub.entity.Videos;
import com.fajar.arabicclub.exception.DataNotFoundException;
import com.fajar.arabicclub.repository.DocumentRepository;
import com.fajar.arabicclub.repository.ImageRepository;
import com.fajar.arabicclub.repository.VideoRepository;
import com.fajar.arabicclub.service.ProgressService;
import com.fajar.arabicclub.util.CollectionUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GalleryService {
	
	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private ImageRepository imageRepository;
	@Autowired
	private VideoRepository videoRepository;
	@Autowired
	private ProgressService progressService;
	@Autowired
	private YoutubeVideoPreviewService youtubeVideoPreviewService;

	public WebResponse getPictures(WebRequest webRequest) {
		int size = webRequest.getFilter().getLimit();
		int page = webRequest.getFilter().getPage();
		log.info("Get pictures page: {}, size:{}", page,size);
		int totalData = 0;
		Page<Images> images = imageRepository.findAll(PageRequest.of(page, size));
		try {
			totalData = imageRepository.findCount().intValue();
		} catch (Exception e) { 
		}
		WebResponse response = new WebResponse();
		response.setItems(CollectionUtil.convertList(images.getContent()));
		response.setTotalData(totalData);
		response.setFilter(webRequest.getFilter());
		return response ;
	}

	public WebResponse getVideos(WebRequest webRequest, HttpServletRequest httpServletRequest) {
		int size = webRequest.getFilter().getLimit();
		int page = webRequest.getFilter().getPage();
		log.info("Get videos page: {}, size:{}", page,size);
		int totalData = 0;
		Page<Videos> videos = videoRepository.findAll(PageRequest.of(page, size));
		try {
			totalData = videoRepository.findCount().intValue();
		} catch (Exception e) { 
		}
		progressService.sendProgress(20, httpServletRequest);
		List<VideosModel> models = youtubeVideoPreviewService.getSnippets(videos.getContent(), httpServletRequest);
		WebResponse response = new WebResponse();
		response.setItemsModel(models);
		response.setTotalData(totalData);
		response.setFilter(webRequest.getFilter());
		return response ;
	}

	public WebResponse getDocuments(WebRequest webRequest) {
		int size = webRequest.getFilter().getLimit();
		int page = webRequest.getFilter().getPage();
		log.info("Get pictures page: {}, size:{}", page,size);
		int totalData = 0;
		List<Documents> list = documentRepository.findByOrderByName(PageRequest.of(page, size));
		try {
			totalData = documentRepository.findCount().intValue();
		} catch (Exception e) { 
		}
		 
		
		for (Documents documents : list) {
			if (documents.getAccessCode() != null && documents.getAccessCode().trim().isEmpty() == false) {
				documents.setAccessCode("PROTECTED");
				documents.setFileName(null);
			} else {
				documents.setAccessCode(null);
			}
		}
		
		WebResponse response = new WebResponse();
		response.setItems(CollectionUtil.convertList(list));
		response.setTotalData(totalData);
		response.setFilter(webRequest.getFilter());
		return response ;
	}

	public WebResponse unlockDocument(Long id, String accessCode) {
		Optional<Documents> record = documentRepository.findById(id);
		if (record.isPresent() == false) {
			throw new DataNotFoundException("Document not foound");
		}
		if (null == record.get().getAccessCode() || record.get().getAccessCode().equals(accessCode)) {
			return WebResponse.builder().message(record.get().getFileName()).build();
		}
		throw new DataNotFoundException("Invalid code");
	}

}
