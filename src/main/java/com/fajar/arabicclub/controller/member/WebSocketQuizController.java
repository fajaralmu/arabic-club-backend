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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@Controller
public class WebSocketQuizController {
	
	@Autowired
	private PublicQuizService publicQuizService;

 

	@PostConstruct
	public void init() { 
		log.info(" @@@@@@@@@------------------WebSocketQuizController----------------@@@@@@@@@@");
	 
	}

	/////////////////////////////////////// WEbsocket
	/////////////////////////////////////// //////////////////////////////////////////

	@MessageMapping("/quiz/answer")
	public void updateQuizHistory(Message<WebRequest> message) throws IOException {
		WebRequest request = message.getPayload();
		log.info("updateQuizHistory: {}", request.getRequestId());
		System.out.println("UPDATE QUIZ HISTORY");
		publicQuizService.updateHistory(request);
	}
	
	 

}
