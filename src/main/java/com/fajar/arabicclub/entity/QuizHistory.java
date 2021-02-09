package com.fajar.arabicclub.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
	private Double lastScore;
	
	
	 

}
