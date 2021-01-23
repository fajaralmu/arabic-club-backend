package com.fajar.arabicclub.service.quiz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

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

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class QuizCreationService {

	@Autowired
	private ProgressService progressService;
	@Autowired
	private EntityRepository entityRepository;
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
	 * @param request
	 * @param httpServletRequest
	 * @return
	 */
	public WebResponse submit(WebRequest request, HttpServletRequest httpServletRequest) {
		WebResponse response = new WebResponse();
		List<QuizQuestion> savedQuestions = new ArrayList<>();
		Quiz quiz = request.getQuiz();

		boolean isNewRecord = quiz.getId() == null;
		
		validateQuizAndQuestions(quiz);
		List<QuizQuestion> existingQuestions = isNewRecord == false ? getExistingQuestion(quiz) : new ArrayList<>();
		Quiz savedQuiz = entityRepository.save(quiz);
		progressService.sendProgress(10, httpServletRequest);
		
		for (QuizQuestion quizQuestion : quiz.getQuestions()) {
			quizQuestion.setQuiz(savedQuiz);
			QuizQuestion savedQuestion = quizDataService.saveQuestionAndItsChoices(quizQuestion);
			if (null != savedQuestion) {
				savedQuestions.add(savedQuestion);
			}
			progressService.sendProgress(1, quiz.getQuestions().size(), 90, httpServletRequest);
		}

		if (!isNewRecord) {
			deleteNotSavedQuestion(existingQuestions, savedQuestions);
		}

		log.info("savedQuestions: {}", savedQuestions.size());
		savedQuiz.setQuestions(savedQuestions);
		response.setQuiz(savedQuiz);
		return response;
	}

	private List<QuizQuestion> getExistingQuestion(Quiz quiz) {
		List<QuizQuestion> existingQuestions = quizQuestionRepository.findByQuiz(quiz);
		return existingQuestions;
	}

	private void deleteNotSavedQuestion(List<QuizQuestion> existingQuestions, List<QuizQuestion> savedQuestions) {
		for (QuizQuestion existingQuestion : existingQuestions) {
			for (QuizQuestion savedQuestion : savedQuestions) {
				if (savedQuestion.getId().equals(existingQuestion.getId())) {
					existingQuestion.setId(null);
				}
			}
		}

		int deletedCount = 0;
		for (QuizQuestion existingQuestion : existingQuestions) {
			if (existingQuestion.getId() != null) {
				deleteQuestionAndChoices(existingQuestion);
				deletedCount++;
			}
		}

		log.info("deletedCount: {}", deletedCount);

	}

	private void deleteQuestionAndChoices(QuizQuestion quizQuestion) {

		List<QuizChoice> choices = quizChoiceRepository.findByQuestionOrderByAnswerCode(quizQuestion);
		for (QuizChoice quizChoice : choices) {
			quizChoiceRepository.delete(quizChoice);
		}
		quizQuestionRepository.delete(quizQuestion);

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
	 * @param id
	 * @param httpServletRequest
	 * @return
	 */
	public WebResponse deleteQuiz(Long id, HttpServletRequest httpServletRequest) {
		Optional<Quiz> existingQuiz = quizRepository.findById(id);
		if (existingQuiz.isPresent() == false) {
			throw new RuntimeException("Existing record not found!");
		}
		deleteQuestions(existingQuiz.get(), httpServletRequest);
		quizRepository.delete(existingQuiz.get());
		progressService.sendProgress(10, httpServletRequest);
		WebResponse response = new WebResponse();
		return response;
	}

	private void deleteQuestions(Quiz existingQuiz, HttpServletRequest httpServletRequest) {

		List<QuizQuestion> questions = quizQuestionRepository.findByQuiz(existingQuiz);
		progressService.sendProgress(10, httpServletRequest);
		log.info("question count: {}", questions.size());
		for (QuizQuestion quizQuestion : questions) {
			List<QuizChoice> choices = quizChoiceRepository.findByQuestionOrderByAnswerCode(quizQuestion);
			deleteChoices(choices);

			quizQuestionRepository.delete(quizQuestion);

			progressService.sendProgress(1, questions.size(), 80, httpServletRequest);
		}
	}

	private void deleteChoices(List<QuizChoice> choices) {
		for (QuizChoice choice : choices) {
			quizChoiceRepository.delete(choice);
		}

	}

}
