package com.fajar.arabicclub.service.quiz;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.arabicclub.constants.ResponseType;
import com.fajar.arabicclub.dto.WebRequest;
import com.fajar.arabicclub.dto.WebResponse;
import com.fajar.arabicclub.dto.model.QuizHistoryModel;
import com.fajar.arabicclub.entity.QuizHistory;
import com.fajar.arabicclub.repository.QuizHistoryRepository;
import com.fajar.arabicclub.service.RealtimeService2;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class QuizHistoryUpdater {

	@Autowired
	private QuizHistoryRepository quizHistoryRepository;
	@Autowired
	private RealtimeService2 realtimeService2;
	@Autowired
	private QuizHistoryService quizHistoryService;

	public void updateStartHistory(QuizHistoryModel request) {

		QuizHistory history = quizHistoryService.getLatestHistory(request.getQuiz().getId(), request.getToken());
		history.setStarted(request.getQuiz().getStartedDate());
		history.setEnded(null);
		history.setScore(null);
		QuizHistory saved = quizHistoryRepository.save(history);

		log.info("start quiz at: {}", saved.getStarted());
	}
	
	public void updateRunningHistory(QuizHistoryModel historyModel) {
		String requestId = historyModel.getRequestId();
		QuizHistory history = quizHistoryService.getLatestHistory(historyModel.getQuiz().getId(), historyModel.getToken());
		String answerData = historyModel.getQuiz().getAnswersData();
		if (null != answerData) {
			log.info("answerData is : {}", answerData);
			history.setAnswerData(answerData);
		} else {
			log.info("answerData is null");
		}
		if (null == history.getStarted()) {
			history.setStarted(historyModel.getQuiz().getStartedDate());
		} 
		if (null != historyModel.getUpdated()) {
			history.setUpdated(historyModel.getUpdated());
		} else {
			history.setUpdated(new Date());
		}
		history.setScore(null);
		quizHistoryRepository.save(history);
		log.info("last quiestion updated at: {}",history.getUpdated() );
		/**
		 * notify
		 */
		WebResponse response = WebResponse.builder().date(history.getUpdated()).type(ResponseType.QUIZ_ANSWER_UPDATE).build();
		realtimeService2.sendUpdate(response, requestId); 
	}
}
