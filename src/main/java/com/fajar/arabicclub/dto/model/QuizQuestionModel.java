package com.fajar.arabicclub.dto.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.fajar.arabicclub.annotation.Dto;
import com.fajar.arabicclub.constants.AnswerCode;
import com.fajar.arabicclub.entity.QuizQuestion;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Dto(entityClass=QuizQuestion.class)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class QuizQuestionModel extends BaseModel<QuizQuestion> {  
	/**
	 * 
	 */
	private static final long serialVersionUID = -4139597408148041771L;
 
	private AnswerCode answerCode;
 
	private String statement;
 
	private QuizModel quiz; 
	private String image;

	@Transient
	private List<QuizChoiceModel> choices;
	@Transient
	private AnswerCode correctChoice;

	@JsonIgnore
	public Long getQuizId() {
		if (quiz == null) {
			return null;
		}
		return quiz.getId();
	}

}
