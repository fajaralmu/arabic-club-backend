package com.fajar.arabicclub.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fajar.arabicclub.annotation.FormField;
import com.fajar.arabicclub.constants.AnswerCode;
import com.fajar.arabicclub.constants.FieldType;
import com.fajar.arabicclub.dto.model.QuizModel;
import com.fajar.arabicclub.entity.setting.SingleImageModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "quiz")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Quiz extends BaseEntity<QuizModel> implements SingleImageModel {
	/**
	* 
	*/
	private static final long serialVersionUID = -1168912843978053906L;

	@Column(nullable = false)
	private String title;
	@Column
	private String description;
	@Column(name="public_quiz", nullable = false)
	private boolean publicQuiz;
	@Column(nullable = false)
	private Long duration;
	@Column(nullable = false)
	private boolean active;
	@Column(nullable = false)
	private boolean repeatable;
	@Column 
	private String image;
	
	//questions
	@Column(name="show_all_question")
	private boolean showAllQuestion;
	@Column(name="question_timered")
	private boolean questionsTimered;
	
	@Column(name="after_completion_message")
	private String afterCompletionMessage;
	@Column(name="access_code")
	private String accessCode;
	@Transient
	@Default
	private boolean available = true;

	@Transient
	@Default
	private List<QuizQuestion> questions = new ArrayList<>();;

	public void addQuestion(QuizQuestion question) {
		if (questions == null) {
			questions = new ArrayList<>();
		}
		questions.add(question);
	}

	@Override
	public QuizModel toModel() {
		QuizModel model = copy(new QuizModel(), "questions");
		if (null != questions) {
			for (QuizQuestion q : questions) {
				model.addQuestion(q.toModel());
			}
		}
		return model;
	}

	public static void main(String[] args) {
		Quiz q = Quiz.builder().description("QUIZ 123").build();
		q.addQuestion(QuizQuestion.builder().correctChoice(AnswerCode.B).build());

		System.out.println(q.toModel());
	}

	public void preventStackOverFlowError() {
		if (null == questions)
			return;
		questions.forEach(q -> {
			q.setQuiz(null);
			q.preventStackOverFlowError();
		});

	}

	public void setChoices(int i, List<QuizChoice> choices) {
		try {
			questions.get(i).setChoices(choices);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	public boolean mapAnswers(QuizHistory quizHistory) {
		if (questions == null) return false;
		Map<Integer, String> mappedAnswer = quizHistory.mappedAnswer();
		for (Integer number : mappedAnswer.keySet()) {
			for (QuizQuestion quizQuestion : questions) {
				if (number.equals(quizQuestion.getNumber())) {
					if (quizQuestion.getEssay() == Boolean.TRUE) {
						quizQuestion.setAnswerEssay(mappedAnswer.get(number));
					} else {
						try {
							quizQuestion.setAnswerCode(AnswerCode.parse(mappedAnswer.get(number)));
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}
			}
		}
		return true;
	}
	
	public boolean hasEssay() {
		if (questions == null) {
			return false;
		}
		for (QuizQuestion quizQuestion : questions) {
			if (quizQuestion.getEssay() == Boolean.TRUE) {
				return true;
			}
		}
		return false;
	}
}
