package com.fajar.arabicclub.repository;

import java.math.BigInteger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fajar.arabicclub.entity.Images;

public interface ImageRepository extends JpaRepository<Images	, Long> {

	@Query("select count(image) from Images image")
	BigInteger findCount(); 

}
