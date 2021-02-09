package com.fajar.arabicclub.service.entity;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.arabicclub.entity.BaseEntity;
import com.fajar.arabicclub.entity.Quiz;
import com.fajar.arabicclub.entity.QuizQuestion;
import com.fajar.arabicclub.repository.QuizQuestionRepository;
import com.fajar.arabicclub.service.quiz.PublicQuizService;

@Service
public class QuizUpdateService extends BaseEntityUpdateService<Quiz> {

	@Autowired
	private QuizQuestionRepository questionRepository;
	
	@Override
	public Quiz saveEntity(Quiz object, boolean newRecord, HttpServletRequest httpServletRequest) throws Exception {
		object = copyNewElement(object, newRecord);
		
		validateEntityFormFields(object, newRecord, httpServletRequest);
		 
		Quiz newEntity = entityRepository.save(object);
		return newEntity;
	}
	public void postFilter(java.util.List<Quiz> objects) {
		if (null == objects || objects.size() == 0) {
			return;
		}
		
		List<QuizQuestion> questions = questionRepository.findByQuizIn(objects);
		PublicQuizService.mapQuizAndQuestions(objects, questions);
		
	};
}
