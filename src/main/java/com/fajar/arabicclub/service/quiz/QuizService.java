package com.fajar.arabicclub.service.quiz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.arabicclub.config.exception.DataNotFoundException;
import com.fajar.arabicclub.dto.WebRequest;
import com.fajar.arabicclub.dto.WebResponse;
import com.fajar.arabicclub.entity.BaseEntity;
import com.fajar.arabicclub.entity.Quiz;
import com.fajar.arabicclub.entity.QuizChoice;
import com.fajar.arabicclub.entity.QuizQuestion;
import com.fajar.arabicclub.repository.EntityRepository;
import com.fajar.arabicclub.repository.QuizChoiceRepository;
import com.fajar.arabicclub.repository.QuizQuestionRepository;
import com.fajar.arabicclub.repository.QuizRepository;
import com.fajar.arabicclub.service.ProgressService;
import com.fajar.arabicclub.service.resources.FileService;

import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class QuizService {

	@Autowired
	private ProgressService progressService;
	@Autowired
	private EntityRepository entityRepository;
	@Autowired
	private QuizRepository quizRepository;
	@Autowired
	private QuizQuestionRepository quizQuestionRepository;
	@Autowired
	private QuizChoiceRepository quizChoiceRepository;
	@Autowired
	private FileService fileService;

	public WebResponse submit(WebRequest request, HttpServletRequest httpServletRequest) {
		WebResponse response = new WebResponse();
		List<QuizQuestion> savedQuestions = new ArrayList<>();
		Quiz quiz = request.getQuiz();
		
		boolean isNewRecord = quiz.getId() == null;
		List<QuizQuestion> existingQuestions = new ArrayList<>();
		if (!isNewRecord) {
			Optional<Quiz> existingQuiz = quizRepository.findById(quiz.getId());
			if (existingQuiz.isPresent() == false) {
				throw new RuntimeException("Existing record not found!");
			}
			existingQuestions = quizQuestionRepository.findByQuiz(quiz);
		}
		validateQuestions(quiz);

		Quiz savedQuiz = entityRepository.save(quiz);
		progressService.sendProgress(10, httpServletRequest);
		for (QuizQuestion quizQuestion : quiz.getQuestions()) {
			quizQuestion.setQuiz(savedQuiz);
			QuizQuestion savedQuestion = saveQuestion(quizQuestion);
			if (null != savedQuestion) {
				savedQuestions.add(savedQuestion);
			}
			progressService.sendProgress(1, quiz.getQuestions().size(), 90, httpServletRequest);
		}
		
		if (!isNewRecord) {
			deleteNotSavedQuestion(existingQuestions, savedQuestions);
		}
		
		log.info("savedQuestions: {}", savedQuestions.size());
		savedQuiz.setQuestions(savedQuestions);
		response.setQuiz(savedQuiz);
		return response;
	}

	private void deleteNotSavedQuestion(List<QuizQuestion> existingQuestions, List<QuizQuestion> savedQuestions) {
		for (QuizQuestion existingQuestion : existingQuestions) {
			for (QuizQuestion savedQuestion : savedQuestions) {
				if (savedQuestion.getId().equals(existingQuestion.getId())) {
					existingQuestion.setId(null);
				}
			}
		}
		
		int deletedCount = 0;
		for (QuizQuestion existingQuestion : existingQuestions) {
			if (existingQuestion.getId() != null) {
				deleteQuestionAndChoices(existingQuestion);
				deletedCount++;
			}
		}
		
		log.info("deletedCount: {}", deletedCount);
		
	}

	private void deleteQuestionAndChoices(QuizQuestion quizQuestion) {
		
		List<QuizChoice> choices = quizChoiceRepository.findByQuestionOrderByAnswerCode(quizQuestion);
		for (QuizChoice quizChoice : choices) {
			quizChoiceRepository.delete(quizChoice);
		}
		quizQuestionRepository.delete(quizQuestion);
		
	}

	private void validateQuestions(Quiz quiz) {

		if (null == quiz.getQuestions() || 0 == quiz.getQuestions().size()) {
			throw new RuntimeException("Empty Question");
		}
	}

	private QuizQuestion saveQuestion(QuizQuestion quizQuestion) {
		if (null == quizQuestion.getChoices() || quizQuestion.getChoices().size() == 0) {
			log.info("quizQuestion.getChoices() empty!");
			return null;
		}
		List<QuizChoice> savedChoices = new ArrayList<>();
		
		quizQuestion.validateNullValues();
		if (quizQuestion.getImage() != null && quizQuestion.getImage().startsWith("data:image")) {
			try {
				String savedFileName = fileService.writeImage(QuizQuestion.class.getSimpleName(), quizQuestion.getImage());
				quizQuestion.setImage(savedFileName);
			} catch (IOException e) {
				e.printStackTrace();
				quizQuestion.setImage(null);
			}
			
		}
		
		QuizQuestion savedQuestion = entityRepository.save(quizQuestion);
		if (null ==savedQuestion) {
			log.info("Question Not Saved");
			return null;
		}
		for (QuizChoice choice : quizQuestion.getChoices()) {
			choice.setQuestion(savedQuestion);
			savedChoices.add(entityRepository.save(choice));
		}

		log.info("savedChoices: {}", savedChoices.size());
		savedQuestion.setChoices(savedChoices);
		return savedQuestion;
	}

	public WebResponse getQuiz(Long id, HttpServletRequest httpServletRequest) throws Exception {
		try {

			WebResponse response = new WebResponse();
			Optional<Quiz> quizOpt = quizRepository.findById(id);
			if (!quizOpt.isPresent()) {
				throw new Exception("Data not found");
			}
			Quiz quiz = quizOpt.get();
			List<QuizQuestion> questions = quizQuestionRepository.findByQuiz(quiz);
			progressService.sendProgress(20, httpServletRequest);

			for (QuizQuestion quizQuestion : questions) {
				List<QuizChoice> choices = quizChoiceRepository.findByQuestionOrderByAnswerCode(quizQuestion);
				quizQuestion.setChoices(choices);

				progressService.sendProgress(1, questions.size(), 80, httpServletRequest);
			}

			quiz.setQuestions(questions);
			response.setQuiz(quiz);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataNotFoundException(e.getMessage());
		}

	}

	public WebResponse deleteQuiz(Long id, HttpServletRequest httpServletRequest) {
		Optional<Quiz> existingQuiz = quizRepository.findById(id);
		if (existingQuiz.isPresent() == false) {
			throw new RuntimeException("Existing record not found!");
		}
		List<QuizQuestion> questions = quizQuestionRepository.findByQuiz(existingQuiz.get());
		progressService.sendProgress(10, httpServletRequest);
		log.info("question count: {}", questions.size());
		for (QuizQuestion quizQuestion : questions) {
			List<QuizChoice> choices = quizChoiceRepository.findByQuestionOrderByAnswerCode(quizQuestion);
			for (QuizChoice choice : choices) {
				quizChoiceRepository.delete(choice);
			}
			quizQuestionRepository.delete(quizQuestion);
			
			progressService.sendProgress(1, questions.size(),80, httpServletRequest);
		}
		quizRepository.delete(existingQuiz.get());
		progressService.sendProgress(10, httpServletRequest);
		WebResponse response = new WebResponse();
		return response ;
	}

}
