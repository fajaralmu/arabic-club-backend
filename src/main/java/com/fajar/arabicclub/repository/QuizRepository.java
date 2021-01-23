package com.fajar.arabicclub.repository;
 
 
import java.math.BigInteger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fajar.arabicclub.entity.Quiz;

public interface QuizRepository extends JpaRepository<Quiz	, Long> { 

	@Query("select count(q) from Quiz q")
	public BigInteger findCountAll();
}
