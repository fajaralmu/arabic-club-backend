package com.fajar.arabicclub.service.quiz;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.arabicclub.entity.Quiz;
import com.fajar.arabicclub.entity.QuizChoice;
import com.fajar.arabicclub.entity.QuizQuestion;
import com.fajar.arabicclub.repository.EntityRepository;
import com.fajar.arabicclub.repository.QuizChoiceRepository;
import com.fajar.arabicclub.repository.QuizQuestionRepository;
import com.fajar.arabicclub.repository.QuizRepository;
import com.fajar.arabicclub.service.ProgressService;
import com.fajar.arabicclub.service.resources.ImageUploadService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class QuizDataService {

	@Autowired
	private EntityRepository entityRepository;
	@Autowired
	private ImageUploadService imageUploadService;
	@Autowired
	private ProgressService progressService; 
	@Autowired
	private QuizRepository quizRepository;
	@Autowired
	private QuizQuestionRepository quizQuestionRepository;
	@Autowired
	private QuizChoiceRepository quizChoiceRepository;
	
	/**
	 * save full quiz
	 * @param quizQuestion
	 * @return
	 */
	public QuizQuestion saveQuestionAndItsChoices(QuizQuestion quizQuestion) {
		if (null == quizQuestion.getChoices() || quizQuestion.getChoices().size() == 0) {
			log.info("quizQuestion.getChoices() empty!");
			return null;
		}

		quizQuestion.validateNullValues();
		imageUploadService.uploadImage(quizQuestion);

		QuizQuestion savedQuestion = entityRepository.save(quizQuestion);
		if (null == savedQuestion) {
			log.info("Question Not Saved");
			return null;
		}

		List<QuizChoice> savedChoices = saveChoices(quizQuestion.getChoices(), savedQuestion);

		log.info("savedChoices: {}", savedChoices.size());
		savedQuestion.setChoices(savedChoices);
		return savedQuestion;
	}
	private List<QuizChoice> saveChoices(List<QuizChoice> choices, QuizQuestion savedQuestion) {
		List<QuizChoice> savedChoices = new ArrayList<>();
		for (QuizChoice choice : choices) {
			choice.setQuestion(savedQuestion);
			QuizChoice savedChoice = saveChoice(choice);
			savedChoices.add(savedChoice);
		}
		return savedChoices;
	}
	private QuizChoice saveChoice(QuizChoice choice) {
		choice.validateNullValues();
		imageUploadService.uploadImage(choice);
		QuizChoice savedChoice = entityRepository.save(choice);
		return savedChoice;
	}
	
	/**
	 * get quiz, its questions, and its choices
	 * @param id
	 * @param httpServletRequest
	 * @param hideAnswer
	 * @return
	 * @throws Exception
	 */
	public Quiz getFullQuiz(Long id, HttpServletRequest httpServletRequest, boolean hideAnswer) throws Exception {
		Optional<Quiz> quizOpt = quizRepository.findById(id);
		if (!quizOpt.isPresent()) {
			throw new Exception("Data not found");
		}
		Quiz quiz = quizOpt.get();
		List<QuizQuestion> questions = quizQuestionRepository.findByQuiz(quiz);
		progressService.sendProgress(20, httpServletRequest);

		for (QuizQuestion quizQuestion : questions) {
			List<QuizChoice> choices = quizChoiceRepository.findByQuestionOrderByAnswerCode(quizQuestion);
			choices.stream().forEach(q->q.setQuestion(null));
			quizQuestion.setChoices(choices);
			quizQuestion.setQuiz(null);
			
			if (hideAnswer) {
				quizQuestion.setAnswerCode(null);
			}

			progressService.sendProgress(1, questions.size(), 80, httpServletRequest);
		}

		quiz.setQuestions(questions);
		return quiz;
	}
}
