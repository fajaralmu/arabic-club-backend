package com.fajar.arabicclub.repository;

import java.math.BigInteger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fajar.arabicclub.entity.Documents;

public interface DocumentRepository extends JpaRepository<Documents	, Long> { 
	@Query("select count(d) from Documents d") 
	BigInteger findCount(); 
}
