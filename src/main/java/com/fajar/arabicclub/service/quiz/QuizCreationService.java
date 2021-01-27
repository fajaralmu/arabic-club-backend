package com.fajar.arabicclub.service.quiz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.arabicclub.config.exception.DataNotFoundException;
import com.fajar.arabicclub.dto.WebRequest;
import com.fajar.arabicclub.dto.WebResponse;
import com.fajar.arabicclub.entity.Quiz;
import com.fajar.arabicclub.entity.QuizChoice;
import com.fajar.arabicclub.entity.QuizQuestion;
import com.fajar.arabicclub.entity.SingleImageModel;
import com.fajar.arabicclub.repository.EntityRepository;
import com.fajar.arabicclub.repository.QuizChoiceRepository;
import com.fajar.arabicclub.repository.QuizQuestionRepository;
import com.fajar.arabicclub.repository.QuizRepository;
import com.fajar.arabicclub.service.ProgressService;
import com.fajar.arabicclub.service.resources.FileService;
import com.fajar.arabicclub.service.resources.ImageRemovalService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class QuizCreationService {

	@Autowired
	private ProgressService progressService;
	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private QuizRepository quizRepository;
	@Autowired
	private QuizQuestionRepository quizQuestionRepository;
	@Autowired
	private QuizChoiceRepository quizChoiceRepository;
	@Autowired
	private QuizDataService quizDataService;

	/**
	 * create or update quiz
	 * 
	 * @param request
	 * @param httpServletRequest
	 * @return
	 */
	public WebResponse submit(WebRequest request, HttpServletRequest httpServletRequest) {
		WebResponse response = new WebResponse();

		Quiz quiz = request.getQuiz();
		validateQuizAndQuestions(quiz);

		Quiz savedQuiz = quizDataService.saveFullQuiz(quiz, httpServletRequest);
		response.setQuiz(savedQuiz);
		return response;
	}

	private void validateQuizAndQuestions(Quiz quiz) {
		if (null == quiz.getQuestions() || 0 == quiz.getQuestions().size()) {
			throw new RuntimeException("Empty Question");
		}
		if (quiz.getId() == null) {
			return;
		}
		Optional<Quiz> existingQuiz = quizRepository.findById(quiz.getId());
		if (existingQuiz.isPresent() == false) {
			throw new RuntimeException("Existing record not found!");
		}

	}

	/*------------------------ get quiz --------------------------*/

	/**
	 * get quiz for admin page
	 * 
	 * @param id
	 * @param httpServletRequest
	 * @return
	 * @throws Exception
	 */
	public WebResponse getQuiz(Long id, HttpServletRequest httpServletRequest) throws Exception {
		try {

			WebResponse response = new WebResponse();
			Quiz fullQuiz = quizDataService.getFullQuiz(id, httpServletRequest, false);
			response.setQuiz(fullQuiz);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataNotFoundException(e.getMessage());
		}

	}

	/*--------------------- Delete ---------------------------*/

	/**
	 * delete quiz, its questions, and its choices
	 * 
	 * @param id
	 * @param httpServletRequest
	 * @return
	 */
	public WebResponse deleteQuiz(Long id, HttpServletRequest httpServletRequest) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		try {
			Optional<Quiz> existingQuiz = quizRepository.findById(id);
			if (existingQuiz.isPresent() == false) {
				throw new RuntimeException("Existing record not found!");
			}
			deleteQuestions(existingQuiz.get(), session, httpServletRequest);
			session.delete(existingQuiz.get());
			progressService.sendProgress(10, httpServletRequest);
			WebResponse response = new WebResponse();
			transaction.commit();
			return response;
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			throw e;
		} finally {
			session.close();
		}
	}

	private void deleteQuestions(Quiz existingQuiz, Session session, HttpServletRequest httpServletRequest) {

		List<QuizQuestion> questions = quizQuestionRepository.findByQuiz(existingQuiz);
		progressService.sendProgress(10, httpServletRequest);
		log.info("question count: {}", questions.size());
		for (QuizQuestion quizQuestion : questions) {
			quizDataService.deleteQuestionAndChoices(quizQuestion, session);
			progressService.sendProgress(1, questions.size(), 80, httpServletRequest);
		}
	}

}
