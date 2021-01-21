package com.fajar.arabicclub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.arabicclub.entity.Documents;

public interface DocumentRepository extends JpaRepository<Documents	, Long> { 

}
