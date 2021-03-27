package com.fajar.arabicclub.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fajar.arabicclub.entity.Documents;

public interface DocumentRepository extends JpaRepository<Documents	, Long> { 
	@Query("select count(d) from Documents d") 
	BigInteger findCount();

	@Query("select d from Documents d order by d.title")
	List<Documents> findByOrderByName(Pageable of); 
}
