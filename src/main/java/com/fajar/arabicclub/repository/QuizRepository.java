package com.fajar.arabicclub.repository;
 
 
import java.math.BigInteger;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fajar.arabicclub.entity.Quiz;

public interface QuizRepository extends JpaRepository<Quiz	, Long> { 

	@Query("select count(q) from Quiz q")
	public BigInteger findCountAll();
	@Query("select count(q) from Quiz q where q.active is true")
	public BigInteger findCountActiveTrue();
	@Query("select  q from Quiz q where q.active is true order by q.title")
	public Page<Quiz> findByActiveTrue(PageRequest of);
	
	@Query("select q from Quiz q order by q.title")
	public Page<Quiz> findByOrderByTitle(PageRequest pageRequest);
	
	default Page<Quiz> findQuizList(boolean isAdmin, PageRequest pageRequest){
		if (isAdmin) {
			return findByOrderByTitle(pageRequest);
		}
		return findByActiveTrue(pageRequest);
	}
	
}
