package com.fajar.arabicclub.service.quiz;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.fajar.arabicclub.dto.AttachmentInfo;
import com.fajar.arabicclub.dto.WebRequest;
import com.fajar.arabicclub.dto.WebResponse;
import com.fajar.arabicclub.entity.Quiz;
import com.fajar.arabicclub.exception.ApplicationException;
import com.fajar.arabicclub.exception.DataNotFoundException;
import com.fajar.arabicclub.repository.QuizRepository;
import com.fajar.arabicclub.service.ProgressNotifier;
import com.fajar.arabicclub.service.ProgressService;
import com.fajar.arabicclub.service.entity.QuizUpdateService;
import com.fajar.arabicclub.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class QuizCreationService {

	@Autowired
	private ProgressService progressService;
	@Autowired
	private QuizRepository quizRepository;
	@Autowired
	private QuizDataService quizDataService;
	@Autowired
	private QuizUpdateService quizUpdateService;
	
	@Value("/resources/quiz_template.xlsx")
	private Resource quizTemplate;

	/**
	 * create or update quiz
	 * 
	 * @param request
	 * @param httpServletRequest
	 * @return
	 */
	public WebResponse submit(WebRequest request, HttpServletRequest httpServletRequest) {
		WebResponse response = new WebResponse();

		Quiz quiz = request.getQuiz().toEntity();
		validateQuizAndQuestions(quiz);

		Quiz savedQuiz = quizDataService.saveFullQuiz(quiz, httpServletRequest);
		savedQuiz.preventStackOverFlowError();
		response.setQuiz(savedQuiz.toModel());
		return response;
	}

	private void validateQuizAndQuestions(Quiz quiz) {
		if (null == quiz.getQuestions() || 0 == quiz.getQuestions().size()) {
			throw new RuntimeException("Empty Question");
		}
		if (quiz.getId() == null) {
			return;
		}
		Optional<Quiz> existingQuiz = quizRepository.findById(quiz.getId());
		if (existingQuiz.isPresent() == false) {
			throw new DataNotFoundException("Existing record not found!");
		}

	}

	/*------------------------ get quiz --------------------------*/

	/**
	 * get quiz for admin page
	 * 
	 * @param id
	 * @param httpServletRequest
	 * @return
	 * @throws Exception
	 */
	public WebResponse getQuiz(Long id, HttpServletRequest httpServletRequest) throws Exception {
		try {

			WebResponse response = new WebResponse();
			Quiz fullQuiz = quizDataService.getFullQuiz(id, httpServletRequest, false);
			response.setQuiz(fullQuiz.toModel());
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataNotFoundException(e.getMessage());
		}

	}

	/*--------------------- Delete ---------------------------*/

	/**
	 * delete quiz, its questions, and its choices
	 * 
	 * @param id
	 * @param httpServletRequest
	 * @return
	 * @throws Exception 
	 */
	public WebResponse deleteQuiz(Long id, HttpServletRequest httpServletRequest) throws Exception {
		 return quizUpdateService.deleteEntity(id, Quiz.class, httpServletRequest);
	}

	public WebResponse uploadquiz(WebRequest webRequest, HttpServletRequest httpServletRequest) {
		
		try {
			log.info("uploadquiz");
			AttachmentInfo attachment = webRequest.getAttachmentInfo();
			String base64 = attachment.getData();
			QuizCreatorByExcel creator = new QuizCreatorByExcel(base64);
			creator.setNotifier(notifier(httpServletRequest));
			Quiz quiz = creator.read();
			Quiz savedQuiz = quizDataService.saveFullQuiz(quiz, httpServletRequest);
			savedQuiz.preventStackOverFlowError();
			
			WebResponse response = new WebResponse();
			response.setQuiz(savedQuiz.toModel());
			return response;
		
		} catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException(e);
		}
	}

	private ProgressNotifier notifier(HttpServletRequest httpServletRequest) {
		 
		return new ProgressNotifier() {
			
			@Override
			public void updateProgress(int progress, int max, int totalProportion) {
				progressService.sendProgress(progress, max, totalProportion, httpServletRequest);
				
			}
		};
	}

	public void downloadTemplate(String quizMode, HttpServletResponse httpServletResponse) throws IOException, InvalidFormatException {

		httpServletResponse.setContentType("text/xls");
		httpServletResponse.setHeader("content-disposition", "attachment;filename=quiztemplate_"+quizMode+".xlsx");
		
		Workbook workbook = WorkbookFactory.create(quizTemplate.getFile());
		
		setTitleAndSheetOrderByMode(quizMode ,workbook);
		
		workbook.write(httpServletResponse.getOutputStream());
	}

	private void setTitleAndSheetOrderByMode(String quizMode, Workbook workbook) {
		 
		if ("essay".equals(quizMode.toLowerCase())) {
			workbook.setSheetOrder("ESSAY", 0);
		} else {
			workbook.setSheetOrder("MULTIPLE_CHOICE", 0);
		}
		Sheet activeSheet = workbook.getSheetAt(0);
		activeSheet.getRow(1).getCell(1).setCellValue("Sample Quiz "+quizMode.toUpperCase()+" "+StringUtil.generateRandomNumber(5));
	}

	 

}
