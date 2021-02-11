package com.fajar.arabicclub.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.fajar.arabicclub.entity.Quiz;
import com.fajar.arabicclub.entity.QuizHistory;
import com.fajar.arabicclub.entity.User;

public interface QuizHistoryRepository extends JpaRepository<QuizHistory	, Long> {

	List<QuizHistory> findByQuizAndUser(Quiz quiz, User user);

	@Query("select h from QuizHistory h where h.user=?1")
	List<QuizHistory> findByUser(User user, PageRequest pageRequest); 
	
	@Query("select count(h) from QuizHistory h where h.user=?1")
	public BigInteger findCountByUser(User user);

	@Query("select h from QuizHistory h where h.user=?1 and h.quiz=?2 order by h.modifiedDate desc")
	Page<QuizHistory> findLatestByUserAndQuiz(User user, Quiz quiz, Pageable pageable);
	 

}
