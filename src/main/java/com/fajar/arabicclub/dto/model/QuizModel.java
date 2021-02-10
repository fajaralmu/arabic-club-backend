package com.fajar.arabicclub.dto.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fajar.arabicclub.annotation.Dto;
import com.fajar.arabicclub.annotation.FormField;
import com.fajar.arabicclub.constants.FieldType;
import com.fajar.arabicclub.entity.Quiz;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter; 
@Dto(creatable = false, entityClass=Quiz.class, updateService = "quizUpdateService") 
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
	@FormField(type = FieldType.FIELD_TYPE_CHECKBOX)
	private boolean active;
	@FormField(type = FieldType.FIELD_TYPE_CHECKBOX)
	private boolean repeatable;
	
	//questions
	@FormField(type = FieldType.FIELD_TYPE_CHECKBOX)
	private boolean showAllQuestion;
	@FormField(type = FieldType.FIELD_TYPE_CHECKBOX)
	private boolean questionsTimered;
	
	private boolean available;
	private List<QuizQuestionModel> questions;
	
	/**
	 * below fields are for view layer
	 */
	@FormField(editable = false, filterable = false)
	@Setter(value=AccessLevel.NONE)
	@Getter(value=AccessLevel.NONE)
	private int questionCount;
	private Date submittedDate;
	private Date startedDate;
	
	
	
	public int getQuestionCount() {
		if (null != questions) return questions.size();
		return questionCount;
	}
	
	public void addQuestion(QuizQuestionModel question) { 
		if (questions == null) {
			questions = new ArrayList<QuizQuestionModel>();
		}
		questions.add(question);
	}
	
	@Override 
	public Quiz  toEntity() {
		Quiz  e = copy(new Quiz(), "questions");
		if (null != questions) {
			questions.forEach(q -> {
				e.addQuestion(q.toEntity());
			});
		}
		return e;
	}
}
