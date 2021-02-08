package com.fajar.arabicclub.dto.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Transient;

import com.fajar.arabicclub.annotation.Dto;
import com.fajar.arabicclub.annotation.FormField;
import com.fajar.arabicclub.constants.FieldType;
import com.fajar.arabicclub.entity.Quiz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor; 
@Dto(editable = false, entityClass=Quiz.class) 
@Data
@Builder	
@AllArgsConstructor
@NoArgsConstructor
public class QuizModel extends BaseModel<Quiz> { 
	  
	/**
	 * 
	 */
	private static final long serialVersionUID = -1954539379442647444L;
	@FormField 
	private String title;
	@FormField 
	private String description; 
	@FormField(type = FieldType.FIELD_TYPE_CHECKBOX) 
	private boolean publicQuiz;
	@FormField(type = FieldType.FIELD_TYPE_NUMBER, labelName = "Duration (Second)") 
	private Long duration;
	
	@Transient
	private List<QuizQuestionModel> questions;
	
	public void addQuestion(QuizQuestionModel question) { 
		if (questions == null) {
			questions = new ArrayList<QuizQuestionModel>();
		}
		questions.add(question);
	}
	
	@Override 
	public Quiz  toEntity() {
		Quiz  e = super.toEntity();
		if (null != questions) {
			questions.forEach(q -> {
				e.addQuestion(q.toEntity());
			});
		}
		return e;
	}
}
