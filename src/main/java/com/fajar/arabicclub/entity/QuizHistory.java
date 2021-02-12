package com.fajar.arabicclub.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fajar.arabicclub.annotation.FormField;
import com.fajar.arabicclub.constants.AnswerCode;
import com.fajar.arabicclub.constants.FieldType;
import com.fajar.arabicclub.dto.model.QuizHistoryModel;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
 
@Entity
@Table(name = "quiz_history")
@Data
@Builder
@AllArgsConstructor
@Slf4j
@NoArgsConstructor
public class QuizHistory extends BaseEntity<QuizHistoryModel>  {

	 /**
	 * 
	 */
	private static final long serialVersionUID = 4503414813358642804L;
	@JoinColumn(name = "user_id", nullable = false)
	@ManyToOne 
	private User user; 
	@JoinColumn(name = "quiz_id", nullable = false)
	@ManyToOne 
	private Quiz quiz; 
 
	@Column(name="last_score")
	private Double score;
	@Column
	private Date started;
	@Column
	private Date ended;
	@Column(columnDefinition = "TEXT", name="answer_data")
	private String answerData;
	
	@JsonIgnore
	public Map<Long, AnswerCode> mappedAnswer() {
		Map<Long, AnswerCode> map = new HashMap<>();;
		if (null == answerData) {
			return map;
		}
		String[] splitted = answerData.split(",");
		for (String string : splitted) {
			try {
				String[] raw = string.split(":");
				Long questionId = Long.valueOf(raw[0]);
				AnswerCode code = AnswerCode.valueOf(raw[1]);
				map.put(questionId, code);
			} catch (Exception e) {
				 
			}
		}
		return map;
	}
	
	public static void main(String[] args) {
		QuizHistory h = QuizHistory.builder().answerData("1:A,2:C,3:D").build();
		System.out.println(h.mappedAnswer());
		
	}
	
	public static QuizHistory create(Quiz q, User u) {
		QuizHistory quizHistory =QuizHistory.builder().quiz(q).user(u).build();
		quizHistory.setStarted(new Date());
		quizHistory.setCreatedDate(new Date());
		return quizHistory;
	}

	public boolean continueLatestQuiz() {
		if (score != null || null == started || null == quiz) return false;
		if (ended != null && ended.before(started)) {
			return false;
		}
		long startedTime = started.getTime();
		long duration = quiz.getDuration();
		long now = new Date().getTime();
		long maximumTime = startedTime + duration*1000;
		long remainingMilisecond =  maximumTime- now;
		
		log.info ("maximumTime: {} = {}", maximumTime, new Date(maximumTime));
		log.info("now: {}, = {}", now, new Date(now));
		log.info("remainingMilisecond: {}", remainingMilisecond);
		
		if (remainingMilisecond < 0 || Double.valueOf(remainingMilisecond/1000)<= 1) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * remaining seconds
	 * @return
	 */
	public int getRemainingDuration() {
		if (null == started || null == quiz) return 0;
		if (ended != null && ended.before(started)) {
			return 0;
		}
		long startedTime = started.getTime();
		long duration = quiz.getDuration();
		long now = new Date().getTime();
		long maximumTime = startedTime + duration*1000;
		long remainingMilisecond =  maximumTime- now;
		return (int) (remainingMilisecond/1000);
	}
	
	 

}
