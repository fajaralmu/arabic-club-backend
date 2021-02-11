package com.fajar.arabicclub.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fajar.arabicclub.annotation.FormField;
import com.fajar.arabicclub.constants.FieldType;
import com.fajar.arabicclub.dto.model.QuizHistoryModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Entity
@Table(name = "quiz_history")
@Data
@Builder
@AllArgsConstructor
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
	
	public static QuizHistory create(Quiz q, User u) {
		QuizHistory quizHistory =QuizHistory.builder().quiz(q).user(u).build();
		quizHistory.setStarted(new Date());
		quizHistory.setCreatedDate(new Date());
		return quizHistory;
	}
	
	 

}
