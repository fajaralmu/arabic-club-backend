package com.fajar.arabicclub.service.quiz;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fajar.arabicclub.constants.AnswerCode;
import com.fajar.arabicclub.dto.Filter;
import com.fajar.arabicclub.dto.QuizResult;
import com.fajar.arabicclub.dto.WebRequest;
import com.fajar.arabicclub.dto.WebResponse;
import com.fajar.arabicclub.entity.Quiz;
import com.fajar.arabicclub.entity.QuizQuestion;
import com.fajar.arabicclub.entity.User;
import com.fajar.arabicclub.exception.ApplicationException;
import com.fajar.arabicclub.exception.DataNotFoundException;
import com.fajar.arabicclub.repository.QuizQuestionRepository;
import com.fajar.arabicclub.repository.QuizRepository;
import com.fajar.arabicclub.service.ProgressService;
import com.fajar.arabicclub.service.SessionValidationService;
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
	@Autowired
	private SessionValidationService sessionValidationService;
	@Autowired
	private QuizHistoryService quizHistoryService;

	/**
	 * get quiz list, paginated
	 * 
	 * @param webRequest
	 * @return
	 */
	public WebResponse getQuizList(WebRequest webRequest, HttpServletRequest httpServletRequest) {

		User user = sessionValidationService.getLoggedUser(httpServletRequest);
		boolean isAdmin = user != null && user.isAdmin();
		Filter filter = webRequest.getFilter();

		log.info("get quiz list page:{}, limit: {}", filter.getPage(), filter.getLimit());
		log.info("is admin: {}", isAdmin);
		PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getLimit());
		Page<Quiz> quizes = quizRepository.findQuizList(isAdmin, pageRequest);
		BigInteger quizCount = isAdmin ? quizRepository.findCountAll() : quizRepository.findCountActiveTrue();

		progressService.sendProgress(10, httpServletRequest);

		List<Quiz> quizList = quizes.getContent();
		List<QuizQuestion> questions = quizList.size() == 0 ? new ArrayList<>()
				: quizQuestionRepository.findByQuizIn(quizList);
		progressService.sendProgress(10, httpServletRequest);

		mapQuizAndQuestions(quizList, questions);
		if (filter.getAvailabilityCheck() == true) {
			mapAvailability(quizList, httpServletRequest);
		}
		WebResponse response = new WebResponse();
		response.setEntities(CollectionUtil.convertList(quizList));
		response.setTotalData(quizCount == null ? 0 : quizCount.intValue());
		return response;
	}

	private void mapAvailability(List<Quiz> quizList, HttpServletRequest httpServletRequest) {
		if (null == quizList || 0 == quizList.size()) {
			return;
		}
		for (Quiz quiz : quizList) {
			boolean allowed = quizHistoryService.isAllowed(quiz, httpServletRequest);
			quiz.setAvailable(allowed);
			progressService.sendProgress(1, quizList.size(), 80, httpServletRequest);
		}

	}

	public static void mapQuizAndQuestions(List<Quiz> quizList, List<QuizQuestion> questions) {

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
			Optional<Quiz> quizRecord = quizRepository.findById(id);
			if (quizRecord.isPresent() == false || quizRecord.get().isActive() != true) {
				throw new DataNotFoundException("Quiz not found");
			}
			WebResponse response = new WebResponse();
			boolean allowed = quizHistoryService.isAllowed(quizRecord.get(), httpServletRequest);
			if (!allowed) {
				throw new ApplicationException("NOT ALLOWED");
			}
			Quiz fullQuiz = quizDataService.getFullQuiz(id, httpServletRequest, true);
			quizHistoryService.updateHistoryStart(quizRecord.get(), httpServletRequest);
			response.setQuiz(fullQuiz.toModel());
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataNotFoundException(e.getMessage());
		}
	}

	/**
	 * submit answers from public
	 * 
	 * @param webRequest
	 * @param httpRequest
	 * @return
	 */
	public WebResponse submitAnswers(WebRequest webRequest, HttpServletRequest httpServletRequest) {
		try {
			Date submittedDate = webRequest.getQuiz().getSubmittedDate();
			Date startedDate = webRequest.getQuiz().getStartedDate();
			final Quiz submittedQuiz = webRequest.getQuiz().toEntity();
			log.info("submitAnswers with id: {}", submittedQuiz.getId());
			WebResponse response = new WebResponse();
			Quiz fullQuiz = quizDataService.getFullQuiz(submittedQuiz.getId(), httpServletRequest, false);

			QuizResult quizResult = calculateAnswers(submittedQuiz, fullQuiz, httpServletRequest);
			quizResult.setStartedDate(startedDate);
			quizResult.setSubmittedDate(submittedDate);

			log.info("started date: {}, submitted date: {}", startedDate, submittedDate);

			quizHistoryService.updateHistoryEnd(quizResult, httpServletRequest);
			response.setQuizResult(quizResult);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataNotFoundException(e.getMessage());
		}
	}

	/**
	 * 
	 * @param submittedQuiz      submitted from client
	 * @param fullQuiz           retrieved from database
	 * @param httpServletRequest
	 * @return quizResult
	 */
	private QuizResult calculateAnswers(Quiz submittedQuiz, Quiz fullQuiz, HttpServletRequest httpServletRequest) {
		QuizResult result = new QuizResult();

		final List<QuizQuestion> questions = fullQuiz.getQuestions();
		int totalQuestion = questions.size(), correctAnswer = 0, wrongAnswer = 0;

		for (int i = 0; i < totalQuestion; i++) {
			try {
				final QuizQuestion actualQuestion = questions.get(i);
				int correctAnswerC = isCorrectAnswer(actualQuestion, submittedQuiz.getQuestions().get(i));
				correctAnswer += correctAnswerC;
				submittedQuiz.getQuestions().get(i).setChoices(actualQuestion.getChoices());
				
			} catch (Exception e) { 
			}
			progressService.sendProgress(1, totalQuestion, 80, httpServletRequest);
			
		}
		wrongAnswer = totalQuestion - correctAnswer;

		result.setSubmittedQuiz(submittedQuiz);
		result.setCorrectAnswer(correctAnswer);
		result.setWrongAnswer(wrongAnswer);
		result.calculateScore();
		log.info("Correct answer: {}", result.getCorrectAnswer());
		log.info("Wrong answer: {}", result.getWrongAnswer());
		log.info("totalQuestion: {}", totalQuestion);
		return result;
	}

	private static int isCorrectAnswer(QuizQuestion actualQuestion, QuizQuestion submittedQuestion) {

		if (submittedQuestion.getId().equals(actualQuestion.getId())) {
			AnswerCode correctAnswer = actualQuestion.getAnswerCode();
			AnswerCode submittedAnswer = submittedQuestion.getAnswerCode();

			submittedQuestion.setCorrectChoice(correctAnswer);
			if (null != submittedAnswer && submittedAnswer.equals(correctAnswer)) {
				return 1;
			}

		}
		return 0;
	}

}
