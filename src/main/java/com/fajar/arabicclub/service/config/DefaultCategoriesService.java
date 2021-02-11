package com.fajar.arabicclub.service.config;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.arabicclub.dto.WebResponse;
import com.fajar.arabicclub.entity.DocumentCategory;
import com.fajar.arabicclub.entity.ImageCategory;
import com.fajar.arabicclub.entity.VideoCategory;
import com.fajar.arabicclub.repository.DocumentCategoryRepository;
import com.fajar.arabicclub.repository.EntityRepository;
import com.fajar.arabicclub.repository.ImageCategoryRepository;
import com.fajar.arabicclub.repository.LessonCategoryRepository;
import com.fajar.arabicclub.repository.VideoCategoryRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DefaultCategoriesService {

	static final String PUBLIC = "public";
	@Autowired
	private EntityRepository entityRepository;
	@Autowired
	private ImageCategoryRepository imageCategoryRepository;
	@Autowired
	private DocumentCategoryRepository documentCategoryRepository;
	@Autowired
	private VideoCategoryRepository videoCategoryRepository;
	@Autowired
	private LessonCategoryRepository lessonCategoryRepository;
	
	@PostConstruct
	public void init() {
		try {
			setDefaultValues();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void setDefaultValues() {
		checkImageCategory();
		checkDocumentCategory();
		checkVideoCategory();
		
	}

	private void checkVideoCategory() {
		VideoCategory category = videoCategoryRepository.findTop1ByCode(PUBLIC);
		if (null != category) return;
		
		category = new VideoCategory(PUBLIC, PUBLIC, PUBLIC);
		entityRepository.save(category);
		log.info("sacing defualt video category");
	}

	private void checkDocumentCategory() {
		DocumentCategory category = documentCategoryRepository.findTop1ByCode(PUBLIC);
		if (null != category) return;
		
		category = new DocumentCategory(PUBLIC, PUBLIC, PUBLIC, "fas fa-folder");
		entityRepository.save(category);
		log.info("sacing defualt document category");
	}

	private void checkImageCategory() {
		ImageCategory category = imageCategoryRepository.findTop1ByCode(PUBLIC);
		if (null != category) return;
		
		category = new ImageCategory(PUBLIC, PUBLIC, PUBLIC, "fas fa-folder");
		entityRepository.save(category);
		log.info("sacing defualt image category");
	}

	public WebResponse getCategories(String code) {
		WebResponse response = new WebResponse();
		List categories = new ArrayList();
		switch (code) {
		case "lesson":
			categories.addAll(lessonCategoryRepository.findAll());
			break;
		case "images":
			categories.addAll(imageCategoryRepository.findAll());
			break;
		case "videos":
			categories.addAll(videoCategoryRepository.findAll());
			break;
		case "document":
			categories.addAll(documentCategoryRepository.findAll());
			break;
		default:
			throw new RuntimeException("Invalid Code :"+code); 
		}
		response.setItems(categories);
		return response;
	}
}
