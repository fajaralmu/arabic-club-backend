package com.fajar.arabicclub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.arabicclub.entity.LessonCategory;
import com.fajar.arabicclub.entity.Lesson;

public interface LessonRepository extends JpaRepository<Lesson	, Long> { 

}
