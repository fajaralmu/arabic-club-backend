package com.fajar.arabicclub.service.quiz;

import java.util.ArrayList;
import java.util.List;

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

	public WebResponse addQuiz(WebRequest request, HttpServletRequest httpServletRequest) {
		WebResponse response = new WebResponse();
		List<QuizQuestion> savedQuestions = new ArrayList<>();
		Quiz quiz = request.getQuiz();
		if (null == quiz.getQuestions() || 0 == quiz.getQuestions().size()) {
			throw new RuntimeException("Empty Question");
		}

		Quiz savedQuiz = entityRepository.save(quiz);
		progressService.sendProgress(10, httpServletRequest);
		for (QuizQuestion quizQuestion : quiz.getQuestions()) {
			quizQuestion.setQuiz(savedQuiz);
			QuizQuestion savedQuestion = saveQuesting(quizQuestion);
			if (null != savedQuestion) {
				savedQuestions.add(savedQuestion);
			}
			progressService.sendProgress(1, quiz.getQuestions().size(), 90, httpServletRequest);
		}

		log.info("savedQuestions: {}",savedQuestions.size());
		savedQuiz.setQuestions(savedQuestions);
		response.setQuiz(savedQuiz);
		return response;
	}

	private QuizQuestion saveQuesting(QuizQuestion quizQuestion) {
		if (null ==quizQuestion.getChoices() || quizQuestion.getChoices().size() ==0) {
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
