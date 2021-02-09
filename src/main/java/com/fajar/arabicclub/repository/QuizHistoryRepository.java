package com.fajar.arabicclub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.arabicclub.entity.Documents;
import com.fajar.arabicclub.entity.QuizHistory;

public interface QuizHistoryRepository extends JpaRepository<QuizHistory	, Long> { 

}
