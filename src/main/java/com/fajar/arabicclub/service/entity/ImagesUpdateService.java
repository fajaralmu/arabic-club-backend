package com.fajar.arabicclub.service.entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.arabicclub.dto.WebResponse;
import com.fajar.arabicclub.entity.Images;
import com.fajar.arabicclub.repository.ImageRepository;
import com.fajar.arabicclub.service.resources.ImageUploadService;
import com.fajar.arabicclub.util.CollectionUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ImagesUpdateService extends BaseEntityUpdateService<Images> {

	@Autowired
	private ImageRepository imagesRepository;
	@Autowired
	private ImageUploadService imageUploadService;
	/**
	 * add & update images
	 * 
	 * @param images
	 * @param newRecord
	 * @return
	 * @throws Exception
	 */
	@Override
	public WebResponse saveEntity(Images baseEntity, boolean newRecord, HttpServletRequest httpServletRequest)
			throws Exception {

		Images images = (Images) copyNewElement(baseEntity, newRecord);
		Optional<Images> dbImages = Optional.empty();
		if (!newRecord) {
			dbImages = imagesRepository.findById(images.getId());
			if (!dbImages.isPresent()) {
				throw new Exception("Existing record not found");
			}
		}
		String imageData = images.getImages();
		if (imageData != null && !imageData.equals("")) {
			log.info("images image will be updated");
			String imageUrl = null;
			if (newRecord) {
				imageUrl = imageUploadService.writeNewImages(images, httpServletRequest);
			} else {
				imageUrl = imageUploadService.updateImages(images, dbImages.get(), httpServletRequest);
			}
			images.setImages(imageUrl);
		} else {
			log.info("Images image wont be updated");
			if (!newRecord) {
				images.setImages(dbImages.get().getImages());
			}
		}

		Images newImages = entityRepository.save(images);

		return WebResponse.builder().entity(newImages).build();
	}
}
