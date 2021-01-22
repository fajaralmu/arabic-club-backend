package com.fajar.arabicclub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.arabicclub.entity.QuizChoice;
import com.fajar.arabicclub.entity.QuizQuestion;

public interface QuizChoiceRepository extends JpaRepository<QuizChoice	, Long> {

	List<QuizChoice> findByQuestion(QuizQuestion quizQuestion); 

}
