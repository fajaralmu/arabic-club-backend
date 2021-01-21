package com.fajar.arabicclub.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fajar.arabicclub.entity.Lesson;

public interface LessonRepository extends JpaRepository<Lesson	, Long> { 

	public List<Lesson> findByCategory_code(String code, Pageable pageable);
	@Query(nativeQuery = true,
			value="select count(lesson.id) from lesson"
					+ " left join lesson_category on lesson_category.id = lesson.id "
					+ " where lesson_category.code = ?1")
	public BigInteger findLessonCountByCategoryCode(String code);
}
