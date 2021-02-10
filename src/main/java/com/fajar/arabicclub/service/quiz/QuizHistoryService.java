package com.fajar.arabicclub.service.quiz;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fajar.arabicclub.dto.Filter;
import com.fajar.arabicclub.dto.QuizResult;
import com.fajar.arabicclub.dto.WebRequest;
import com.fajar.arabicclub.dto.WebResponse;
import com.fajar.arabicclub.dto.model.BaseModel;
import com.fajar.arabicclub.entity.Quiz;
import com.fajar.arabicclub.entity.QuizHistory;
import com.fajar.arabicclub.entity.User;
import com.fajar.arabicclub.exception.ApplicationException;
import com.fajar.arabicclub.repository.QuizHistoryRepository;
import com.fajar.arabicclub.repository.QuizRepository;
import com.fajar.arabicclub.service.SessionValidationService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class QuizHistoryService {

	@Autowired
	private QuizHistoryRepository quizHistoryRepository;
	@Autowired
	private QuizRepository quizRepository;
	@Autowired
	private SessionValidationService sessionValidationService;
	
	public boolean isAllowed(Quiz quiz,HttpServletRequest httpServletRequest) {
		Optional<Quiz> quizOpt = quizRepository.findById(quiz.getId());
		if (quizOpt.isPresent() == false) {
			log.info("QUIZ not found");
			return false;
		}
		User user = sessionValidationService.getLoggedUser(httpServletRequest);
		if (null == user) {
			log.info("user not found");
			return false;
		}
		boolean recordExist = isHistoryExist(quiz, user);
		if (!quiz.isRepeatable() && recordExist) {
			log.info("isRepeatable=FALSE & recordExist");
			return false;
		}
		return true;
	}
	 
	
	public QuizHistory updateHistoryEnd(QuizResult result, HttpServletRequest httpServletRequest) {
		User user = sessionValidationService.getLoggedUser(httpServletRequest);
		if (null == user) {
			log.info("user not found");
			return null;
		}
		List<QuizHistory> records = quizHistoryRepository.findByQuizAndUser(result.getSubmittedQuiz(), user);
		if (null == records && records.size() > 0) return null;
		
		QuizHistory history = records.get(0);
		history.setModifiedDate(new Date());
		if (result.getStartedDate() != null) {
			history.setStarted(result.getStartedDate());
		}
		if (result.getSubmittedDate()!=null) {
			history.setEnded(result.getSubmittedDate());
		}else {
			history.setEnded(new Date());
		}
		
		history.setScore(result.getScore());
		return quizHistoryRepository.save(history);
	}
	public QuizHistory updateHistoryStart(Quiz quiz, HttpServletRequest httpServletRequest) {
		User user = sessionValidationService.getLoggedUser(httpServletRequest);
		if (null == user) {
			log.info("user not found");
			return null;
		}
		List<QuizHistory> records = quizHistoryRepository.findByQuizAndUser(quiz, user);
		QuizHistory history;
		if (null == records || records.size() == 0) {
			history = QuizHistory.create(quiz, user);
		}else {

			history = records.get(0);
		}
		history.setStarted(new Date());
		return quizHistoryRepository.save(history);
	}

	public boolean isHistoryExist(Quiz quiz, User user) {
		List<QuizHistory> records = quizHistoryRepository.findByQuizAndUser(quiz, user);
		return records!=null && records.size() > 0;
	}


	public WebResponse getHistory(WebRequest webRequest, HttpServletRequest httpServletRequest) {
		;
		User user = sessionValidationService.getLoggedUser(httpServletRequest);
		if (null == user) {
			throw new ApplicationException("User not logged in");
		}
		Filter filter = webRequest.getFilter();
		PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getLimit());
		List<QuizHistory> histories = quizHistoryRepository.findByUser(user, pageRequest);
		BigInteger totalData = quizHistoryRepository.findCountByUser(user);
		
		WebResponse response = new WebResponse();
		response.setTotalData(totalData==null?0:totalData.intValue());
		response.setEntities((histories));
		return response ;
	}
}
