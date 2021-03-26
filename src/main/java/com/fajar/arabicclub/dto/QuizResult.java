package com.fajar.arabicclub.dto;

import java.io.Serializable;
import java.util.Date;

import com.fajar.arabicclub.entity.Quiz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7914795420031765969L;
	
	private Quiz submittedQuiz;
	private Date startedDate;
	private Date submittedDate;
	private int correctAnswer;
	private int wrongAnswer;
	private int totalQuestion;
	private double score;
	
	private String message;
	
	public void calculateScore() {
		Double correct = Double.valueOf(correctAnswer);
		Double wrong = Double.valueOf(wrongAnswer);
		
		setTotalQuestion(correctAnswer+ wrongAnswer);
		setScore((correct*100.d)/(correct+wrong));
	}
}
