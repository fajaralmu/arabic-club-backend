package com.fajar.arabicclub.service.entity;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.arabicclub.dto.WebResponse;
import com.fajar.arabicclub.entity.Quiz;
import com.fajar.arabicclub.entity.QuizQuestion;
import com.fajar.arabicclub.entity.User;
import com.fajar.arabicclub.exception.ApplicationException;
import com.fajar.arabicclub.repository.QuizQuestionRepository;
import com.fajar.arabicclub.service.ProgressService;
import com.fajar.arabicclub.service.SessionValidationService;
import com.fajar.arabicclub.service.quiz.PublicQuizService;
import com.fajar.arabicclub.service.quiz.QuizDataService;

@Service
public class QuizUpdateService extends BaseEntityUpdateService<Quiz> {

	@Autowired
	private QuizQuestionRepository questionRepository;
	@Autowired
	private ProgressService progressService;
	@Autowired
	private SessionValidationService sessionValidationService;
	@Autowired
	private QuizDataService quizDataService;
	@Autowired
	private SessionFactory sessionFactory;
	
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
	
	@Override
	public WebResponse deleteEntity(Long id, Class _class, HttpServletRequest httpServletRequest) throws Exception {
		
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		try {
			Quiz quizRecord = (Quiz) session.get(Quiz.class, id);
			User user = sessionValidationService.getLoggedUser(httpServletRequest);
			progressService.sendProgress(10, httpServletRequest);
			deleteQuizHistory(user.getId(), id, session);
			progressService.sendProgress(10, httpServletRequest);
			deleteQuestions(quizRecord, session);
			progressService.sendProgress(70, httpServletRequest);
			session.delete(quizRecord);
			tx.commit();
			progressService.sendProgress(10, httpServletRequest);
			return new WebResponse();
		} catch (Exception ex) {
			if (null != tx) {
				tx.rollback();
			}
			throw new ApplicationException(ex);
		}
	}
	private void deleteQuestions(Quiz quizRecord, Session session) {
		 List<QuizQuestion> questions = questionRepository.findByQuiz(quizRecord);
		 if (null == questions || questions.size() == 0) {
			 return;
		 }
		 quizDataService.deleteQuestions(questions, session);
		
	}
	private void deleteQuizHistory(Long userId, Long quizId, Session session) {
		 
		Query query = session.createQuery("delete from QuizHistory h where h.quiz.id = ?  and h.user.id = ? ");
		
		query.setLong(0, quizId);
		query.setLong(1, userId);
		query.executeUpdate();
	}
}
