package com.fajar.arabicclub.controller;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fajar.arabicclub.annotation.CustomRequestInfo;
import com.fajar.arabicclub.dto.WebRequest;
import com.fajar.arabicclub.dto.WebResponse;
import com.fajar.arabicclub.entity.setting.EntityProperty;
import com.fajar.arabicclub.service.LogProxyFactory;
import com.fajar.arabicclub.service.entity.EntityManagementPageService;
import com.fajar.arabicclub.service.entity.EntityService;
import com.fajar.arabicclub.service.quiz.QuizService;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@RequestMapping("/api/app/quiz")
@Slf4j
public class RestQuizManagementController extends BaseController {

	@Autowired
	private QuizService quizService;
	 

	public RestQuizManagementController() {
		log.info("------------------Rest Entity Controller-----------------");
	}

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	@PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse add(@RequestBody WebRequest request, HttpServletRequest httpRequest) {
		log.info("add quiz ");
		return quizService.addQuiz(request, httpRequest);
	}
	@PostMapping(value = "/getquiz/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse getQuiz(@PathVariable(name="id") Long id, HttpServletRequest httpRequest) {
		log.info("getQuiz quiz {}", id);
		return quizService.getQuiz(id, httpRequest);
	}
 

}
