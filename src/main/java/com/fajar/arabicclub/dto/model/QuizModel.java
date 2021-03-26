package com.fajar.arabicclub.dto.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;

import com.fajar.arabicclub.annotation.Dto;
import com.fajar.arabicclub.annotation.FormField;
import com.fajar.arabicclub.constants.AnswerCode;
import com.fajar.arabicclub.constants.FieldType;
import com.fajar.arabicclub.entity.Quiz;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j; 
@Dto(creatable = false, updateService = "quizUpdateService") 
@Data
@Builder	
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@JsonInclude(value =Include.NON_NULL)
public class QuizModel extends BaseModel<Quiz>   { 
	  
	/**
	 * 
	 */
	private static final long serialVersionUID = -1954539379442647444L;
	@FormField 
	private String title;
	@FormField ( required = false)
	private String description; 
	@FormField(type = FieldType.FIELD_TYPE_IMAGE)
	private String image;
	@FormField(type = FieldType.FIELD_TYPE_CHECKBOX) 
	private boolean publicQuiz;
	@FormField(type = FieldType.FIELD_TYPE_NUMBER, labelName = "Duration (Second)")
	@Getter(value=AccessLevel.NONE)
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
	
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA, required =  false)
	private String afterCompletionMessage;
	@FormField(required = false)
	private String accessCode;
	
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
	private Map<Integer, String> mappedAnswer;
//	private Map<Integer, AnswerCode> mappedAnswer;
	 
	
	public String[] getAnswers() {
		if (null == mappedAnswer) {
			return null;
		}
		String[] answers = new String[mappedAnswer.keySet().size()];
		int i = 0;
		for (Integer questionNumber : mappedAnswer.keySet()) {
			answers[i] = questionNumber+":"+mappedAnswer.get(questionNumber);
			log.info("answers[i]: {}",answers[i] );
			i++;
		}
		return answers;
	}
	
	
	
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
	
	public Long getDuration() {
		if (isQuestionsTimered() &&  getQuestionCount() > 0) {
			Long durationTotal = 0L;
			for (QuizQuestionModel q : questions) {
				durationTotal+=q.getDuration();
			}
			return durationTotal;
		}
		return duration;
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



	public String getAnswersData() {
		String[] answers = getAnswers();
		if (null == answers) return null;
		return String.join(",", answers);
	}
}
