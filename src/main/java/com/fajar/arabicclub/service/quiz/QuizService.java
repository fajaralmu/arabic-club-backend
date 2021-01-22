package com.fajar.arabicclub.service.quiz;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.arabicclub.dto.WebRequest;
import com.fajar.arabicclub.dto.WebResponse;
import com.fajar.arabicclub.entity.Quiz;
import com.fajar.arabicclub.entity.QuizChoice;
import com.fajar.arabicclub.entity.QuizQuestion;
import com.fajar.arabicclub.repository.EntityRepository;
import com.fajar.arabicclub.repository.QuizChoiceRepository;
import com.fajar.arabicclub.repository.QuizQuestionRepository;
import com.fajar.arabicclub.repository.QuizRepository;
import com.fajar.arabicclub.service.ProgressService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class QuizService {

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

	public WebResponse submit(WebRequest request, HttpServletRequest httpServletRequest) {
		WebResponse response = new WebResponse();
		List<QuizQuestion> savedQuestions = new ArrayList<>();
		Quiz quiz = request.getQuiz();
		
		boolean isNewRecord = quiz.getId() == null;
		List<QuizQuestion> existingQuestions = new ArrayList<>();
		if (!isNewRecord) {
			Optional<Quiz> existingQuiz = quizRepository.findById(quiz.getId());
			if (existingQuiz.isPresent() == false) {
				throw new RuntimeException("Existing record not found!");
			}
			existingQuestions = quizQuestionRepository.findByQuiz(quiz);
		}
		validateQuestions(quiz);

		Quiz savedQuiz = entityRepository.save(quiz);
		progressService.sendProgress(10, httpServletRequest);
		for (QuizQuestion quizQuestion : quiz.getQuestions()) {
			quizQuestion.setQuiz(savedQuiz);
			QuizQuestion savedQuestion = saveQuestiion(quizQuestion);
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
		
		List<QuizChoice> choices = quizChoiceRepository.findByQuestion(quizQuestion);
		for (QuizChoice quizChoice : choices) {
			quizChoiceRepository.delete(quizChoice);
		}
		quizQuestionRepository.delete(quizQuestion);
		
	}

	private void validateQuestions(Quiz quiz) {

		if (null == quiz.getQuestions() || 0 == quiz.getQuestions().size()) {
			throw new RuntimeException("Empty Question");
		}
	}

	private QuizQuestion saveQuestiion(QuizQuestion quizQuestion) {
		if (null == quizQuestion.getChoices() || quizQuestion.getChoices().size() == 0) {
			log.info("quizQuestion.getChoices() empty!");
			return null;
		}
		List<QuizChoice> savedChoices = new ArrayList<>();
		QuizQuestion savedQuestion = entityRepository.save(quizQuestion);
		for (QuizChoice choice : quizQuestion.getChoices()) {
			choice.setQuestion(savedQuestion);
			savedChoices.add(entityRepository.save(choice));
		}

		log.info("savedChoices: {}", savedChoices.size());
		savedQuestion.setChoices(savedChoices);
		return savedQuestion;
	}

	public WebResponse getQuiz(Long id, HttpServletRequest httpServletRequest) {
		try {

			WebResponse response = new WebResponse();
			Quiz quiz = quizRepository.findById(id).get();
			List<QuizQuestion> questions = quizQuestionRepository.findByQuiz(quiz);
			progressService.sendProgress(20, httpServletRequest);

			for (QuizQuestion quizQuestion : questions) {
				List<QuizChoice> choices = quizChoiceRepository.findByQuestion(quizQuestion);
				quizQuestion.setChoices(choices);

				progressService.sendProgress(1, questions.size(), 80, httpServletRequest);
			}

			quiz.setQuestions(questions);
			response.setQuiz(quiz);
			return response;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}

	}

}
