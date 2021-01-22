package com.fajar.arabicclub.service.lessons;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fajar.arabicclub.dto.WebRequest;
import com.fajar.arabicclub.dto.WebResponse;
import com.fajar.arabicclub.entity.Lesson;
import com.fajar.arabicclub.entity.LessonCategory;
import com.fajar.arabicclub.repository.LessonCategoryRepository;
import com.fajar.arabicclub.repository.LessonRepository;
import com.fajar.arabicclub.util.CollectionUtil;

@Service
public class LessonService {

	@Autowired
	private LessonRepository lessonRepository;
	@Autowired
	private LessonCategoryRepository lessonCategoryRepository;

	public WebResponse getLessons(String categoryCode, WebRequest webRequest) {
		int size = webRequest.getFilter().getLimit();
		int page = webRequest.getFilter().getPage();
		LessonCategory category = lessonCategoryRepository.findTop1ByCode(categoryCode);
		if (null == category) {
			throw new RuntimeException("Category Not Found");
		}
		Object filter = webRequest.getFilter().getFieldsFilter().get(("value"));
		if (null == filter) {
			filter = "";
		}
		List<Lesson> lessons = lessonRepository.findByCategoryCodeAndFilter(categoryCode, String.valueOf(filter), PageRequest.of(page, size));
		Integer totalData = 0;
		try {
			totalData = lessonRepository.findLessonCountByCategoryCodeAndFIlter(categoryCode,String.valueOf(filter)).intValue();
		} catch (Exception e) { 
		}
		return WebResponse.builder().entity(category).totalData(totalData ).entities(CollectionUtil.convertList(lessons)).build();
	}

}
