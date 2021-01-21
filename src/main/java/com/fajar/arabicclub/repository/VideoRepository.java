package com.fajar.arabicclub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.arabicclub.entity.Videos;

public interface VideoRepository extends JpaRepository<Videos	, Long> { 

}
