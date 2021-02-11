package com.fajar.arabicclub.repository;

import java.math.BigInteger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fajar.arabicclub.entity.Videos;

public interface VideoRepository extends JpaRepository<Videos	, Long> {

	@Query("select count(v) from Videos v") 
	BigInteger findCount(); 

}
