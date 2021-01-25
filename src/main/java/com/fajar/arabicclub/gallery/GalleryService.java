package com.fajar.arabicclub.gallery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fajar.arabicclub.dto.WebRequest;
import com.fajar.arabicclub.dto.WebResponse;
import com.fajar.arabicclub.entity.Images;
import com.fajar.arabicclub.repository.ImageRepository;
import com.fajar.arabicclub.util.CollectionUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GalleryService {
	
	@Autowired
	private ImageRepository imageRepository;

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
		response.setEntities(CollectionUtil.convertList(images.getContent()));
		response.setTotalData(totalData);
		response.setFilter(webRequest.getFilter());
		return response ;
	}

}
