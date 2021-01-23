package com.fajar.arabicclub.service.quiz;

import java.math.BigInteger;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fajar.arabicclub.config.exception.DataNotFoundException;
import com.fajar.arabicclub.dto.Filter;
import com.fajar.arabicclub.dto.WebRequest;
import com.fajar.arabicclub.dto.WebResponse;
import com.fajar.arabicclub.entity.Quiz;
import com.fajar.arabicclub.repository.QuizRepository;
import com.fajar.arabicclub.util.CollectionUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PublicQuizService {
	
	@Autowired
	private QuizRepository quizRepository;
	@Autowired
	private QuizDataService quizDataService;

	/**
	 * get quiz list, paginated
	 * @param webRequest
	 * @return
	 */
	public WebResponse getQuizList(WebRequest webRequest) {

		Filter filter = webRequest.getFilter();
		
		log.info("get quiz list page:{}, limit: {}", filter.getPage(), filter.getLimit());
		Page<Quiz> quizes = quizRepository.findAll(PageRequest.of(filter.getPage(), filter.getLimit()));
		BigInteger quizCount = quizRepository.findCountAll();
		List<Quiz> quizList = quizes.getContent();
		
		WebResponse response = new WebResponse();
		response.setEntities(CollectionUtil.convertList(quizList));
		response.setTotalData(quizCount == null ? 0: quizCount.intValue());
		return response;
	}

	/**
	 * get full quiz for challenge
	 * @param id
	 * @param httpServletRequest
	 * @return
	 */
	public WebResponse getQuiz(Long id, HttpServletRequest httpServletRequest) {
		try {

			WebResponse response = new WebResponse();
			Quiz fullQuiz = quizDataService.getFullQuiz(id, httpServletRequest, true);
			response.setQuiz(fullQuiz);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataNotFoundException(e.getMessage());
		}
	}

}
