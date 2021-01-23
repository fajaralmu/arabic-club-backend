package com.fajar.arabicclub.controller;

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

import com.fajar.arabicclub.dto.WebRequest;
import com.fajar.arabicclub.dto.WebResponse;
import com.fajar.arabicclub.service.LogProxyFactory;
import com.fajar.arabicclub.service.config.DefaultCategoriesService;
import com.fajar.arabicclub.service.lessons.LessonService;
import com.fajar.arabicclub.service.quiz.PublicQuizService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/public/quiz")
public class RestPublicQuizController extends BaseController {
 
	@Autowired
	private PublicQuizService publicQuizService;
	 
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	public RestPublicQuizController() {
		log.info("----------------------RestPublicQuizController-------------------");
	}

	 
	@PostMapping(value = "/list", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse getQuizList(@RequestBody WebRequest webRequest, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
		
		log.info("getQuizList");
		WebResponse response = publicQuizService.getQuizList(webRequest);
		return response;
	}
	@PostMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse getQuizList(@PathVariable Long id, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
		
		log.info("getQuiz By ID : {}", id);
		WebResponse response = publicQuizService.getQuiz(id, httpRequest);
		return response;
	}
	 
	
}
