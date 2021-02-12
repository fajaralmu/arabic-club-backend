package com.fajar.arabicclub.controller.member;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.fajar.arabicclub.dto.WebRequest;
import com.fajar.arabicclub.dto.WebResponse;
import com.fajar.arabicclub.service.quiz.PublicQuizService;
import com.fajar.arabicclub.service.quiz.QuizHistoryService;
import com.fajar.arabicclub.service.quiz.QuizHistoryUpdater;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@Controller
public class WebSocketQuizController {
	
	@Autowired
	private QuizHistoryUpdater quizHistoryService;

 

	@PostConstruct
	public void init() { 
		log.info(" @@@@@@@@@------------------WebSocketQuizController----------------@@@@@@@@@@");
	 
	}

	/////////////////////////////////////// WEbsocket
	/////////////////////////////////////// //////////////////////////////////////////
	@MessageMapping("/quiz/start")
	public void startQuizNotif(Message<WebRequest> message) throws IOException {
		WebRequest request = message.getPayload();
		 
		try {
			log.info("start: {}", request.getQuizHistory().getRequestId());
			quizHistoryService.updateStartHistory(request.getQuizHistory());
		} catch (Exception e) {
			log.error("ERROR startQuizNotif:{}", e);
		}
		
	}
	@MessageMapping("/quiz/answer")
	public void updateQuizNotif(Message<WebRequest> message) throws IOException {
		WebRequest request = message.getPayload();
		
		try {
			log.info("updateQuizHistory: {}", request.getQuizHistory().getRequestId());
			quizHistoryService.updateRunningHistory(request.getQuizHistory());
		} catch (Exception e) {
			log.error("ERROR updateQuizNotif:{}", e);
		}
	}
	
	 

}
