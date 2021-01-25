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
import com.fajar.arabicclub.service.ProgressService;
import com.fajar.arabicclub.service.resources.ImageUploadService;
import com.fajar.arabicclub.util.CollectionUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ImagesUpdateService extends BaseEntityUpdateService<Images> {

	@Autowired
	private ImageRepository imagesRepository;
	@Autowired
	private ProgressService progressService;

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
				imageUrl = writeNewImages(images, httpServletRequest);
			} else {
				imageUrl = updateImages(images, dbImages.get(), httpServletRequest);
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

	private String writeNewImages(Images images, HttpServletRequest httpServletRequest) {
		String imageData = images.getImages();
		String[] rawImageList = imageData.split("~");
		if (rawImageList == null || rawImageList.length == 0) {
			return null;
		}
		List<String> imageUrls = new ArrayList<>();
		for (int i = 0; i < rawImageList.length; i++) {
			String base64Image = rawImageList[i];
			if (base64Image == null || base64Image.equals(""))
				continue;
			try {
				String imageName = fileService.writeImage(images.getClass().getSimpleName(), base64Image,
						httpServletRequest);
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
		images.setImages(imageUrlArray);

		return imageUrlArray;
	}

	private String updateImages(Images images, Images dbImages, HttpServletRequest httpServletRequest) {
		String imageData = images.getImages();
		final String[] rawImageList = imageData.split("~");
		if (rawImageList == null || rawImageList.length == 0 || dbImages == null) {
			return null;
		}
		final boolean oldValueExist = dbImages.getImages() != null && dbImages.getImages().split("~").length > 0;
		final String[] oldValueStringArr = oldValueExist ? images.getImages().split("~") : new String[] {};
		final List<String> imageUrls = new ArrayList<>();
		// loop
		log.info("rawImageList length: {}", rawImageList.length);
		for (int i = 0; i < rawImageList.length; i++) {
			final String rawImage = rawImageList[i];
			if (rawImage == null || rawImage.equals(""))
				continue;
			String imageName = null;
			if (isBase64(rawImage)) {
				try {
					imageName = fileService.writeImage(images.getClass().getSimpleName(), rawImage, httpServletRequest);
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
		images.setImages(imageUrlArray);

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
