package com.fajar.arabicclub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.arabicclub.entity.ImageCategory;
import com.fajar.arabicclub.entity.LessonCategory;

public interface ImageCategoryRepository extends JpaRepository<ImageCategory, Long> {

	ImageCategory findTop1ByCode(String public1);

	 

}
