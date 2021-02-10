package com.fajar.arabicclub.controller.member;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fajar.arabicclub.annotation.CustomRequestInfo;
import com.fajar.arabicclub.controller.BaseController;
import com.fajar.arabicclub.dto.WebRequest;
import com.fajar.arabicclub.dto.WebResponse;
import com.fajar.arabicclub.service.LogProxyFactory;
import com.fajar.arabicclub.service.quiz.PublicQuizService;
import com.fajar.arabicclub.service.quiz.QuizHistoryService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/member/quiz")
public class RestMemberQuizController extends BaseController{
	
	@Autowired
	private PublicQuizService publicQuizService;
	@Autowired
	private QuizHistoryService quizHistoryService;
	 
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	public RestMemberQuizController() {
		log.info("----------------------RestMemberQuizController-------------------");
	}

	 
	@PostMapping(value = "/list", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse getQuizList(@RequestBody WebRequest webRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpResponse) throws IOException {
		
		log.info("get Quiz List");
		WebResponse response = publicQuizService.getQuizList(webRequest, httpServletRequest);
		return response;
	}
	@PostMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse getQuizList(@PathVariable Long id, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
		
		log.info("getQuiz By ID : {}", id);
		WebResponse response = publicQuizService.getQuiz(id, httpRequest);
		return response;
	}
	@PostMapping(value = "/submit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse submitAnswers(@RequestBody WebRequest webRequest, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
		
		log.info("submitAnswers");
		WebResponse response = publicQuizService.submitAnswers(webRequest, httpRequest);
		return response;
	}
	@PostMapping(value = "/history", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse getHistory(@RequestBody WebRequest webRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpResponse) throws IOException {
		
		log.info("get Quiz List");
		WebResponse response = quizHistoryService.getHistory(webRequest, httpServletRequest);
		return response;
	}

}
