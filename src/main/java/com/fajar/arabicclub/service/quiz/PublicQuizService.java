package com.fajar.arabicclub.service.quiz;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.arabicclub.constants.AnswerCode;
import com.fajar.arabicclub.dto.Filter;
import com.fajar.arabicclub.dto.QuizResult;
import com.fajar.arabicclub.dto.WebRequest;
import com.fajar.arabicclub.dto.WebResponse;
import com.fajar.arabicclub.dto.model.QuizHistoryModel;
import com.fajar.arabicclub.entity.Quiz;
import com.fajar.arabicclub.entity.QuizHistory;
import com.fajar.arabicclub.entity.QuizQuestion;
import com.fajar.arabicclub.entity.User;
import com.fajar.arabicclub.exception.ApplicationException;
import com.fajar.arabicclub.exception.DataNotFoundException;
import com.fajar.arabicclub.repository.QuizQuestionRepository;
import com.fajar.arabicclub.repository.QuizRepository;
import com.fajar.arabicclub.service.ProgressService;
import com.fajar.arabicclub.service.SessionValidationService;
import com.fajar.arabicclub.service.entity.MasterDataService;
import com.fajar.arabicclub.service.entity.MasterDataService.FilterResult;
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
	@Autowired
	private MasterDataService masterDataService;

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
		filter.validateFieldsFilter();
		log.info("get quiz list page:{}, limit: {}", filter.getPage(), filter.getLimit());
		log.info("is admin: {}", isAdmin);

		if (!isAdmin) {
			filter.putFilter("active", "true");
		}

		FilterResult filterResult = masterDataService.filterEntities(filter, Quiz.class);
		progressService.sendProgress(10, httpServletRequest);

		List<Quiz> quizList = (filterResult.getList());
		int quizCount = filterResult.getCount();
		List<QuizQuestion> questions = quizList.size() == 0 ? new ArrayList<>()
				: quizQuestionRepository.findByQuizIn(quizList);
		progressService.sendProgress(10, httpServletRequest);

		mapQuizAndQuestions(quizList, questions);
		if (filter.getAvailabilityCheck() == true) {
			mapAvailability(quizList, httpServletRequest);
		}
		WebResponse response = new WebResponse();
		response.setItems(CollectionUtil.convertList(quizList));
		response.setTotalData(quizCount);
		return response;
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
			QuizHistory history = synchronizeLatestHistory(fullQuiz, httpServletRequest);
			quizHistoryService.updateHistoryStart(quizRecord.get(), httpServletRequest);
			response.setQuiz(fullQuiz.toModel());
			if (null != history) {
				response.setQuizHistory(copyHistory(history));
				response.setMessage("Success getting quiz synchronized with latest history");
			} else {
				response.setMessage("Success getting quiz");
			}

			return response;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataNotFoundException(e.getMessage());
		}
	}

	private QuizHistoryModel copyHistory(QuizHistory history) {
		 
		QuizHistoryModel model = QuizHistoryModel.builder().started(history.getStarted())
				.remainingDuration(history.getRemainingDuration())
				.maxQuestionNumber(history.getMaxQuestionNumber())
				.updated(history.getUpdated())
				.build();
		model.setId(history.getId());
		return model;
	}



	private QuizHistory synchronizeLatestHistory(Quiz fullQuiz, HttpServletRequest httpServletRequest) {
		QuizHistory history = quizHistoryService.getLatestHistory(fullQuiz,
				sessionValidationService.getLoggedUser(httpServletRequest));
		if (null == history) {
			log.info("Latest history not found");
			return null;
		}
		boolean continueLatestQuiz = history.continueLatestQuiz();
		if (!continueLatestQuiz) {
			log.info("continueLatestQuiz not allowed");
			return null;
		}
		fullQuiz.mapAnswers(history);
		log.info("Sync with latest history success");
		return history;

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
		
		if (submittedQuiz.hasEssay() == false) {
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
			result.setCorrectAnswer(correctAnswer);
			result.setWrongAnswer(wrongAnswer);
		} else {
			result.setDisplayScore(false);
		}
		result.setSubmittedQuiz(submittedQuiz);
		result.setMessage(submittedQuiz.getAfterCompletionMessage());
		
		result.calculateScore();
		log.info("Correct answer: {}", result.getCorrectAnswer());
		log.info("Wrong answer: {}", result.getWrongAnswer());
		
		return result;
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
					if (question.getQuizId() != null && question.getQuizId().equals(quiz.getId())) {
						question.setAnswerCode(null);
						question.setQuiz(null);
						quiz.addQuestion(question);
					}
				}
			});
		}
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



	public WebResponse validateAccessCode(Long id, HttpServletRequest httpRequest) {
		Optional<Quiz> quiz = quizRepository.findById(id);
		if (quiz.isPresent()==false) {
			throw new DataNotFoundException("Not found");
		}
		String code = httpRequest.getParameter("code");
		
		log.info("requested code: {}, actual code: {}", code,  quiz.get().getAccessCode());
		
		if (quiz.get().getAccessCode() == null || quiz.get().getAccessCode().equals(code)) {
			return new WebResponse();
		}
		throw new ApplicationException("Code invalid");
	}

}
