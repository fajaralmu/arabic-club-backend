package com.fajar.arabicclub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.arabicclub.entity.LessonCategory;

public interface LessonCategoryRepository extends JpaRepository<LessonCategory, Long> {

	LessonCategory findTop1ByCode(String categoryCode);

	 
}
