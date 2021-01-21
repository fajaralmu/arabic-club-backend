package com.fajar.arabicclub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.arabicclub.entity.DocumentCategory;

public interface DocumentCategoryRepository extends JpaRepository<DocumentCategory, Long> {

	DocumentCategory findTop1ByCode(String public1);

	 
}
