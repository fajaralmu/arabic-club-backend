package com.fajar.arabicclub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.arabicclub.entity.VideoCategory;

public interface VideoCategoryRepository extends JpaRepository<VideoCategory, Long> {

	VideoCategory findTop1ByCode(String public1);

	 
}
