package com.fajar.arabicclub.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fajar.arabicclub.constants.AnswerCode;
import com.fajar.arabicclub.dto.model.QuizChoiceModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
 
@Entity
@Table(name = "quiz_choice")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class QuizChoice extends BaseEntity<QuizChoiceModel>  implements SingleImageModel{

	/**
	* 
	*/
	private static final long serialVersionUID = 3494963248002164943L;
	@Column(nullable = false,name="answer_code") 
	@Enumerated(EnumType.STRING)
	private AnswerCode answerCode;
	
	@Column(nullable = false)
	private String statement;
	@Column
	private String image;
	
	@JoinColumn(name = "question_id", nullable = false)
	@ManyToOne
	@JsonIgnore
	private QuizQuestion question; 
  
	

}
