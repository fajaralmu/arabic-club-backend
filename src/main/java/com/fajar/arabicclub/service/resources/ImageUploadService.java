package com.fajar.arabicclub.service.resources;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.arabicclub.entity.QuizChoice;
import com.fajar.arabicclub.entity.SingleImageModel;

@Service
public class ImageUploadService {
	@Autowired
	private FileService fileService;

	public  void uploadImage(SingleImageModel singleImageModel) {
		String image = singleImageModel.getImage();
		if (image != null && image.startsWith("data:image")) {
			try {
				String savedFileName = fileService.writeImage(QuizChoice.class.getSimpleName(), image);
				singleImageModel.setImage(savedFileName);
			} catch (IOException e) {
				e.printStackTrace();
				singleImageModel.setImage(null);
			}

		}
	}
}
