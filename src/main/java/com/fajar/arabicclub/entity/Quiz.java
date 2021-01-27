package com.fajar.arabicclub.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fajar.arabicclub.annotation.Dto;
import com.fajar.arabicclub.annotation.FormField;
import com.fajar.arabicclub.constants.FieldType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor; 
@Dto(editable = false)
@Entity
@Table (name="quiz")
@Data
@Builder	
@AllArgsConstructor
@NoArgsConstructor
public class Quiz extends BaseEntity {/**
	 * 
	 */
	private static final long serialVersionUID = -1168912843978053906L; 
	  
	@FormField
	@Column(unique = true, nullable = false)
	private String title;
	@FormField
	@Column
	private String description; 
	@FormField(type = FieldType.FIELD_TYPE_CHECKBOX)
	@Column
	private boolean publicQuiz;
	@FormField(type = FieldType.FIELD_TYPE_NUMBER, lableName = "Duration (Second)")
	@Column
	private Long duration;
	
	@Transient
	private List<QuizQuestion> questions;
	
	public void addQuestion(QuizQuestion question) { 
		if (questions == null) {
			questions = new ArrayList<QuizQuestion>();
		}
		questions.add(question);
	}
}
