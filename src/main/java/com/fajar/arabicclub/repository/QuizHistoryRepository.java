package com.fajar.arabicclub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.arabicclub.entity.Quiz;
import com.fajar.arabicclub.entity.QuizHistory;
import com.fajar.arabicclub.entity.User;

public interface QuizHistoryRepository extends JpaRepository<QuizHistory	, Long> {

	List<QuizHistory> findByQuizAndUser(Quiz quiz, User user); 

}
