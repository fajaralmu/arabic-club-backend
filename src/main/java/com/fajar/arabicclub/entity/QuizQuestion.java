package com.fajar.arabicclub.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fajar.arabicclub.constants.AnswerCode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Entity
@Table(name = "quiz_question")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizQuestion extends BaseEntity implements SingleImageModel{

	/**
	* 
	*/
	private static final long serialVersionUID = 3494963248002164943L;
	@Column(nullable = false,name="answer_code") 
	@Enumerated(EnumType.STRING)
	private AnswerCode answerCode;
	
	@Column(nullable = false) 
	private String statement;
	
	@JoinColumn(name = "quiz_id", nullable = false)
	@ManyToOne 
	private Quiz quiz; 
	
	@Column
	private String image;
	
	@Transient
	private List<QuizChoice> choices;
  

}
