package com.fajar.arabicclub.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fajar.arabicclub.constants.AnswerCode;
import com.fajar.arabicclub.dto.model.QuizModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

 
@Entity
@Table(name = "quiz")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Quiz extends BaseEntity<QuizModel> {
	/**
	* 
	*/
	private static final long serialVersionUID = -1168912843978053906L;

	@Column(unique = true, nullable = false)
	private String title;
	@Column
	private String description;
	@Column
	private boolean publicQuiz;
	@Column(nullable = false)
	private Long duration;
	@Column 
	private Boolean active;

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
}
