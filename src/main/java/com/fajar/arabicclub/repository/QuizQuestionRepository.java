package com.fajar.arabicclub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fajar.arabicclub.entity.Quiz;
import com.fajar.arabicclub.entity.QuizQuestion;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion	, Long> {

	@Query("select q from QuizQuestion q where q.quiz = ?1 order by q.number asc")
	List<QuizQuestion> findByQuiz(Quiz quiz);

	@Query("select q from QuizQuestion q where q.quiz in ?1")
	List<QuizQuestion> findByQuizIn(List<Quiz> quizList); 

	
}
