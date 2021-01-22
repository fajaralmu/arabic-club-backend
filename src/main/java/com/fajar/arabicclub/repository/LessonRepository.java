package com.fajar.arabicclub.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fajar.arabicclub.entity.Lesson;

public interface LessonRepository extends JpaRepository<Lesson	, Long> { 

	@Query("select lesson from Lesson lesson"
			+ " left join lesson.category category "
			+ " where category.code = ?1 and "
			+ " ( lower(lesson.content) like lower(concat('%', ?2,'%')) or  lower(lesson.title) like lower(concat('%', ?2,'%')) or  lower(lesson.description) like lower(concat('%', ?2,'%')) )")
	public List<Lesson> findByCategoryCodeAndFilter(String code, String filter, Pageable pageable);
	@Query(nativeQuery = true,
			value="select count(lesson.id) from lesson"
					+ " left join lesson_category on lesson_category.id = lesson.category_id "
					+ " where lesson_category.code = ?1")
	public BigInteger findLessonCountByCategoryCode(String code);
	@Query("select count(lesson) from Lesson lesson"
			+ " left join lesson.category category "
			+ " where category.code = ?1 and "
			+ " ( lower(lesson.content) like lower(concat('%', ?2,'%')) or  lower(lesson.title) like lower(concat('%', ?2,'%')) or  lower(lesson.description) like lower(concat('%', ?2,'%')) )")
	public BigInteger findLessonCountByCategoryCodeAndFIlter(String code, String filter);
}
