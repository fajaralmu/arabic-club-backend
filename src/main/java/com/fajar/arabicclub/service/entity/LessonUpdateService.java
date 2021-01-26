package com.fajar.arabicclub.service.entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.arabicclub.dto.WebResponse;
import com.fajar.arabicclub.entity.Lesson;
import com.fajar.arabicclub.repository.LessonRepository;
import com.fajar.arabicclub.service.resources.ImageUploadService;
import com.fajar.arabicclub.util.CollectionUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LessonUpdateService extends BaseEntityUpdateService<Lesson> {

	@Autowired
	private LessonRepository lessonRepository;  
	@Autowired
	private ImageUploadService imageUploadService;

	/**
	 * add & update lesson
	 * 
	 * @param lesson
	 * @param newRecord
	 * @return
	 * @throws Exception
	 */
	@Override
	public WebResponse saveEntity(Lesson baseEntity, boolean newRecord, HttpServletRequest httpServletRequest) throws Exception {

		Lesson lesson = (Lesson) copyNewElement(baseEntity, newRecord);
		Optional<Lesson> dbLesson = Optional.empty();
		if (!newRecord) {
			dbLesson = lessonRepository.findById(lesson.getId());
			if (!dbLesson.isPresent()) {
			 
				throw new Exception("Existing record not found");
			}
		} else {
			lesson.setUser(getLoggedUser(httpServletRequest));
		}
		final	String imageData = lesson.getBannerImages();
		if (imageData != null && !imageData.equals("")) {
			log.info("lesson image will be updated");
			String imageUrl = null;
			if (newRecord) {
				imageUrl = imageUploadService.writeNewImages(lesson, httpServletRequest);
			} else {
				imageUrl = imageUploadService.updateImages(lesson, dbLesson.get(), httpServletRequest);
			}
			lesson.setBannerImages(imageUrl);
		} else {
			log.info("Lesson image wont be updated");
			if (!newRecord) {
				lesson.setBannerImages(dbLesson.get().getBannerImages());
			} 
		}

		Lesson newLesson = entityRepository.save(lesson);
		 

		return WebResponse.builder().entity(newLesson).build();
	}

	
}
