package com.fajar.arabicclub.service.quiz;

import java.math.BigInteger;
import java.util.List;
import java.util.function.Consumer;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fajar.arabicclub.dto.Filter;
import com.fajar.arabicclub.dto.QuizResult;
import com.fajar.arabicclub.dto.WebRequest;
import com.fajar.arabicclub.dto.WebResponse;
import com.fajar.arabicclub.entity.Quiz;
import com.fajar.arabicclub.entity.QuizQuestion;
import com.fajar.arabicclub.exception.DataNotFoundException;
import com.fajar.arabicclub.repository.QuizQuestionRepository;
import com.fajar.arabicclub.repository.QuizRepository;
import com.fajar.arabicclub.service.ProgressService;
import com.fajar.arabicclub.util.CollectionUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PublicQuizService {

	@Autowired
	private QuizRepository quizRepository;
	@Autowired
	private QuizDataService quizDataService;
	@Autowired
	private QuizQuestionRepository quizQuestionRepository;
	@Autowired
	private ProgressService progressService;

	/**
	 * get quiz list, paginated
	 * 
	 * @param webRequest
	 * @return
	 */
	public WebResponse getQuizList(WebRequest webRequest) {

		Filter filter = webRequest.getFilter();

		log.info("get quiz list page:{}, limit: {}", filter.getPage(), filter.getLimit());
		Page<Quiz> quizes = quizRepository.findAll(PageRequest.of(filter.getPage(), filter.getLimit()));
		BigInteger quizCount = quizRepository.findCountAll();
		List<Quiz> quizList = quizes.getContent();
		List<QuizQuestion> questions = quizQuestionRepository.findByQuizIn(quizList);
		
		mapQuizAndQuestions(quizList, questions);

		WebResponse response = new WebResponse();
		response.setEntities(CollectionUtil.convertList(quizList));
		response.setTotalData(quizCount == null ? 0 : quizCount.intValue());
		return response;
	}

	private static void mapQuizAndQuestions(List<Quiz> quizList, List<QuizQuestion> questions) {
		 
		for (final Quiz quiz : quizList) {
			questions.forEach(new Consumer<QuizQuestion>() { 
				@Override
				public void accept(QuizQuestion question) {
					 if (question.getQuizId().equals(quiz.getId())) {
						 question.setAnswerCode(null);
						 quiz.addQuestion(question);
					 }
				}
			});
		}
	}

	/**
	 * get full quiz for challenge
	 * 
	 * @param id
	 * @param httpServletRequest
	 * @return
	 */
	public WebResponse getQuiz(Long id, HttpServletRequest httpServletRequest) {
		try {
			log.info("take quiz with id: {}", id);
			WebResponse response = new WebResponse();
			Quiz fullQuiz = quizDataService.getFullQuiz(id, httpServletRequest, true);
			response.setQuiz(fullQuiz.toModel());
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataNotFoundException(e.getMessage());
		}
	}

	/**
	 * submit answers from public
	 * @param webRequest
	 * @param httpRequest
	 * @return
	 */
	public WebResponse submitAnswers(WebRequest webRequest, HttpServletRequest httpServletRequest) {
		try {
			final Quiz submittedQuiz = webRequest.getQuiz().toEntity();
			log.info("submitAnswers with id: {}", submittedQuiz.getId());
			WebResponse response = new WebResponse();
			Quiz fullQuiz = quizDataService.getFullQuiz(submittedQuiz.getId(), httpServletRequest, false);
			
			QuizResult quizResult = calculateAnswers(submittedQuiz, fullQuiz, httpServletRequest);
			 
			response.setQuizResult(quizResult);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataNotFoundException(e.getMessage());
		}
	}

	/**
	 * 
	 * @param submittedQuiz submitted from client
	 * @param fullQuiz retrieved from database
	 * @param httpServletRequest
	 * @return quizResult
	 */
	private QuizResult calculateAnswers(Quiz submittedQuiz, Quiz fullQuiz, HttpServletRequest httpServletRequest) {
		QuizResult result = new QuizResult();
		
		final List<QuizQuestion> questions = fullQuiz.getQuestions();
		int totalQuestion = questions.size(), correctAnswer = 0, wrongAnswer = 0;
		
		for (int i = 0; i < totalQuestion; i++) {
			final QuizQuestion actualQuestion = questions.get(i);
			int correctAnswerC = isCorrectAnswer(actualQuestion, submittedQuiz.getQuestions());
			correctAnswer+=correctAnswerC;
			
			progressService.sendProgress(1, totalQuestion, 80, httpServletRequest);
		}
		wrongAnswer = totalQuestion - correctAnswer;
		
		result.setSubmittedQuiz(submittedQuiz);
		result.setCorrectAnswer(correctAnswer);
		result.setWrongAnswer(wrongAnswer);
		result.calculateScore();
		log.info("Correct answer: {}" ,result.getCorrectAnswer());
		log.info("Wrong answer: {}" ,result.getWrongAnswer());
		log.info("totalQuestion: {}", totalQuestion);
		return result ;
	}

	private int isCorrectAnswer(QuizQuestion actualQuestion, List<QuizQuestion> submittedQuestions) {
		innerLoop: for (int j = 0; j < submittedQuestions.size(); j++) {
			final QuizQuestion submittedQuestion =  submittedQuestions.get(j);
			
			if (submittedQuestion.getId().equals(actualQuestion.getId())) {
				submittedQuestion.setCorrectChoice(actualQuestion.getAnswerCode());
				if (submittedQuestion.getAnswerCode().equals(actualQuestion.getAnswerCode())) {
					return 1;
				}
				break innerLoop;
			}
		};
		return 0;
	}

}
