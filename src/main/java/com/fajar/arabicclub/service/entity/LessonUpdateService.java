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
import com.fajar.arabicclub.util.CollectionUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LessonUpdateService extends BaseEntityUpdateService<Lesson> {

	@Autowired
	private LessonRepository lessonRepository;  

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
				imageUrl = writeNewImages(lesson, httpServletRequest);
			} else {
				imageUrl = updateImages(lesson, dbLesson, httpServletRequest);
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

	private String writeNewImages(Lesson lesson, HttpServletRequest httpServletRequest) {
		String[] rawImageList = lesson.getBannerImages().split("~");
		if (rawImageList == null || rawImageList.length == 0) {
			return null;
		}
		List<String> imageUrls = new ArrayList<>();
		for (int i = 0; i < rawImageList.length; i++) {
			String base64Image = rawImageList[i];
			if (base64Image == null || base64Image.equals(""))
				continue;
			try {
				String imageName = fileService.writeImage(lesson.getClass().getSimpleName(), base64Image, httpServletRequest);
				if (null != imageName) {
					imageUrls.add(imageName);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (imageUrls.size() == 0) {
			return null;
		}

		String[] arrayOfString = imageUrls.toArray(new String[] {});
		CollectionUtil.printArray(arrayOfString);

		String imageUrlArray = String.join("~", arrayOfString);
		lesson.setBannerImages(imageUrlArray);

		return imageUrlArray;
	}

	private String updateImages(Lesson lesson, Optional<Lesson> dbLesson, HttpServletRequest httpServletRequest) {
		final String[] rawImageList = lesson.getBannerImages().split("~");
		if (rawImageList == null || rawImageList.length == 0 || dbLesson.isPresent() == false) {
			return null;
		}
		final boolean oldValueExist = dbLesson.get().getBannerImages() != null
				&& dbLesson.get().getBannerImages().split("~").length > 0;
		final String[] oldValueStringArr = oldValueExist ? lesson.getBannerImages().split("~") : new String[] {};
		final List<String> imageUrls = new ArrayList<>();
		//loop
		log.info("rawImageList length: {}", rawImageList.length);
		for (int i = 0; i < rawImageList.length; i++) {
			final String rawImage = rawImageList[i];
			if (rawImage == null || rawImage.equals(""))
				continue;
			String imageName = null;
			if (isBase64(rawImage)) {
				try {
					imageName = fileService.writeImage(lesson.getClass().getSimpleName(), rawImage);
					log.info("saved base64 image {}", imageName);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {

				if (oldValueExist && inArray(rawImage, oldValueStringArr)) {
					imageName = rawImage;
				}
			}

			if (imageName != null) {
				imageUrls.add(imageName);
			}
		}
		if (imageUrls.size() == 0) {
			return null;
		}

		String[] arrayOfString = imageUrls.toArray(new String[] {});
		CollectionUtil.printArray(arrayOfString);

		String imageUrlArray = String.join("~", arrayOfString);
		lesson.setBannerImages(imageUrlArray);

		return imageUrlArray;
	}

	private boolean inArray(String imageName, String[] array) {
		for (int i = 0; i < array.length; i++) {
			if (imageName.equals(array[i]))
				return true;
		}
		
		return false;
	}

	private boolean isBase64(String rawImage) {

		return rawImage.startsWith("data:image");
	}
}
