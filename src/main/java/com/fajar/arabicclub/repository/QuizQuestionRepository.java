package com.fajar.arabicclub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.arabicclub.entity.Documents;
import com.fajar.arabicclub.entity.Quiz;
import com.fajar.arabicclub.entity.QuizChoice;
import com.fajar.arabicclub.entity.QuizQuestion;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion	, Long> { 

}
