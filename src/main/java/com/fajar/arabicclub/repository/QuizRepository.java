package com.fajar.arabicclub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.arabicclub.entity.Quiz;

public interface QuizRepository extends JpaRepository<Quiz	, Long> { 

}
