package com.fajar.arabicclub.service.quiz;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.arabicclub.entity.Quiz;
import com.fajar.arabicclub.entity.QuizChoice;
import com.fajar.arabicclub.entity.QuizQuestion;
import com.fajar.arabicclub.repository.DatabaseProcessor;
import com.fajar.arabicclub.repository.QuizChoiceRepository;
import com.fajar.arabicclub.repository.QuizQuestionRepository;
import com.fajar.arabicclub.repository.QuizRepository;
import com.fajar.arabicclub.service.ProgressService;
import com.fajar.arabicclub.service.resources.ImageRemovalService;
import com.fajar.arabicclub.service.resources.ImageUploadService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class QuizDataService {

	@Autowired
	private ImageUploadService imageUploadService;
	@Autowired
	private ImageRemovalService imageRemovalService;
	@Autowired
	private ProgressService progressService;
	@Autowired
	private QuizRepository quizRepository;
	@Autowired
	private QuizQuestionRepository quizQuestionRepository;
	@Autowired
	private QuizChoiceRepository quizChoiceRepository;
	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * save full quiz
	 * 
	 * @param quizQuestion
	 * @return
	 */
	private QuizQuestion saveQuestionAndItsChoices(QuizQuestion quizQuestion, Session session) {
		if (null == quizQuestion.getChoices() || quizQuestion.getChoices().size() == 0) {
			log.info("quizQuestion.getChoices() empty!");
			return null;
		}

		quizQuestion.validateNullValues();
		imageUploadService.uploadImage(quizQuestion);
		
		QuizQuestion savedQuestion = DatabaseProcessor.save(quizQuestion, session);
		if (null == savedQuestion) {
			log.info("Question Not Saved");
			return null;
		}

		List<QuizChoice> savedChoices = saveChoices(quizQuestion.getChoices(), savedQuestion, session);

		log.info("savedChoices: {}", savedChoices.size());
		savedQuestion.setChoices(savedChoices);
		return savedQuestion;
	}

	private List<QuizChoice> saveChoices(List<QuizChoice> choices, QuizQuestion savedQuestion, Session session) {
		List<QuizChoice> savedChoices = new ArrayList<>();
		for (QuizChoice choice : choices) {
			choice.setQuestion(savedQuestion);
			QuizChoice savedChoice = saveChoice(choice, session);
			savedChoices.add(savedChoice);
		}
		return savedChoices;
	}

	private QuizChoice saveChoice(QuizChoice choice, Session session) {
		choice.validateNullValues();
		imageUploadService.uploadImage(choice);
		return DatabaseProcessor.save(choice, session);
	}

	/**
	 * get quiz, its questions, and its choices
	 * 
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
			choices.stream().forEach(q -> q.setQuestion(null));
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

	public Quiz saveFullQuiz(Quiz quiz, HttpServletRequest httpServletRequest) {
		final List<QuizQuestion> savedQuestions = new ArrayList<>();
		final List<QuizQuestion> submittedQuestions = quiz.getQuestions();
		boolean isNewRecord = quiz.getId() == null;
		List<QuizQuestion> existingQuestions = isNewRecord == false ? getExistingQuestion(quiz) : new ArrayList<>();

		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		try {
			Quiz savedQuiz = DatabaseProcessor.save(quiz, session);
			progressService.sendProgress(10, httpServletRequest);

			
			for (QuizQuestion quizQuestion : submittedQuestions ) {
				quizQuestion.setQuiz(quiz);
				QuizQuestion savedQuestion = saveQuestionAndItsChoices(quizQuestion, session);
				if (null != savedQuestion) {
					savedQuestions.add(savedQuestion);
				}
				progressService.sendProgress(1, quiz.getQuestions().size(), 90, httpServletRequest);
			}

			if (!isNewRecord) {
				deleteNotSavedQuestion(existingQuestions, savedQuestions, session);
			}

			log.info("savedQuestions: {}", savedQuestions.size());
			savedQuiz.setQuestions(savedQuestions);
			
			transaction.commit();
			return savedQuiz;
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			throw e;
		} finally {
			session.close();
		}

	}

	private List<QuizQuestion> getExistingQuestion(Quiz quiz) {
		List<QuizQuestion> existingQuestions = quizQuestionRepository.findByQuiz(quiz);
		return existingQuestions;
	}

	private void deleteNotSavedQuestion(List<QuizQuestion> existingQuestions, List<QuizQuestion> savedQuestions, Session session) {
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
				deleteQuestionAndChoices(existingQuestion, session);
				deletedCount++;
			}
		}

		log.info("deletedCount: {}", deletedCount);

	}

	public void deleteQuestionAndChoices(QuizQuestion quizQuestion, Session session) {

		List<QuizChoice> choices = quizChoiceRepository.findByQuestionOrderByAnswerCode(quizQuestion);
		for (QuizChoice quizChoice : choices) {
			imageRemovalService.removeImage(quizChoice.getImage());
			session.delete(quizChoice);
		}

		imageRemovalService.removeImage(quizQuestion.getImage());
		session.delete(quizQuestion);

	}
}
