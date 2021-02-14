package com.fajar.arabicclub.dto.model;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fajar.arabicclub.annotation.Dto;
import com.fajar.arabicclub.constants.AnswerCode;
import com.fajar.arabicclub.entity.QuizChoice;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Dto(entityClass=QuizChoice.class)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class QuizChoiceModel extends BaseModel<com.fajar.arabicclub.entity.QuizChoice>{

	/**
	* 
	*/
	private static final long serialVersionUID = 3494963248002164943L;
	 
	private AnswerCode answerCode; 
	private String statement; 
	private String image; 
	@JsonIgnore
	private QuizQuestionModel question; 
  
	

}
