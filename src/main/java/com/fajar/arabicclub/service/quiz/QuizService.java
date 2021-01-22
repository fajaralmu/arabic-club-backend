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
import com.fajar.arabicclub.service.ProgressService;

@Service
public class QuizService {

	@Autowired
	private ProgressService progressService;
	@Autowired
	private EntityRepository entityRepository;
	
	public WebResponse addQuiz(WebRequest request, HttpServletRequest httpServletRequest) {
		WebResponse response = new WebResponse();
		List<QuizQuestion> savedQuestion = new ArrayList<>();
		Quiz quiz = request.getQuiz();
		Quiz savedQuiz = entityRepository.save(quiz);
		progressService.sendProgress(10, httpServletRequest);
		
		for (QuizQuestion quizQuestion : quiz.getQuestions()) {
			quizQuestion.setQuiz(savedQuiz);
			savedQuestion.add(saveQuesting(quizQuestion));
			progressService.sendProgress(1, quiz.getQuestions().size(), 90, httpServletRequest);
		}
		
		savedQuiz.setQuestions(savedQuestion);
		response.setQuiz(savedQuiz);
		return response ;
	}

	private QuizQuestion saveQuesting(QuizQuestion quizQuestion) {
		QuizQuestion savedQuestion = entityRepository.save(quizQuestion);
		for (QuizChoice choice : quizQuestion.getChoices()) {
			choice.setQuestion(savedQuestion);
			entityRepository.save(choice);
		}
		return savedQuestion;
	}

	
}
