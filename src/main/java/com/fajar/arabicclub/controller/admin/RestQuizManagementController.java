package com.fajar.arabicclub.controller.admin;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
import com.fajar.arabicclub.service.quiz.QuizCreationService;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@RequestMapping("/api/app/quiz")
@Slf4j
public class RestQuizManagementController extends BaseController {

	@Autowired
	private QuizCreationService quizService;
	 @Autowired
	 private PublicQuizService publicQuizService;

	public RestQuizManagementController() {
		log.info("------------------Rest Entity Controller-----------------");
	}

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	@PostMapping(value = "/list", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	 
	public WebResponse list(@RequestBody WebRequest request, HttpServletRequest httpRequest) {
		log.info("list of quiz ");
		return publicQuizService.getQuizList(request, httpRequest);
	}
	@PostMapping(value = "/submit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse add(@RequestBody WebRequest request, HttpServletRequest httpRequest) {
		log.info("add quiz ");
		return quizService.submit(request, httpRequest);
	}
	@PostMapping(value = "/getquiz/{id}" , produces = MediaType.APPLICATION_JSON_VALUE)
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse getQuiz(@PathVariable(name="id") Long id, HttpServletRequest httpRequest) throws Exception{
		log.info("getQuiz id {}", id);
		return quizService.getQuiz(id, httpRequest);
	}
	@PostMapping(value = "/deletequiz/{id}" , produces = MediaType.APPLICATION_JSON_VALUE)
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse deletequiz(@PathVariable(name="id") Long id,HttpServletRequest httpRequest) throws Exception {
		log.info("Delete id {}", id);
		return quizService.deleteQuiz(id, httpRequest);
	}
	@PostMapping(value = "/uploadquiz" , produces = MediaType.APPLICATION_JSON_VALUE)
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse uploadquiz(@RequestBody WebRequest webRequest,HttpServletRequest httpRequest) throws Exception {
		 
		return quizService.uploadquiz(webRequest, httpRequest);
	}
	
	
 

}
