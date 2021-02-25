package com.fajar.arabicclub.dto.model;

import java.util.Date;

import com.fajar.arabicclub.annotation.Dto;
import com.fajar.arabicclub.annotation.FormField;
import com.fajar.arabicclub.constants.FieldType;
import com.fajar.arabicclub.entity.QuizHistory;
import com.fajar.arabicclub.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(value = Include.NON_NULL)
@Dto(editable = false)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizHistoryModel extends BaseModel<QuizHistory> {

	/**
	* 
	*/
	private static final long serialVersionUID = 4503414813358642804L;
	@FormField(optionItemName = "displayName")
	private UserModel user;
	@FormField(optionItemName = "title")
	private QuizModel quiz;

	@FormField(type = FieldType.FIELD_TYPE_DATETIME)
	private Date started;
	@FormField(type = FieldType.FIELD_TYPE_DATETIME)
	private Date ended;
	@FormField(filterable = false)
	@Setter(value = AccessLevel.NONE)
	@Getter(value = AccessLevel.NONE)
	private String quizDuration;
	@FormField(filterable = false)
	@Setter(value = AccessLevel.NONE)
	@Getter(value = AccessLevel.NONE)
	private String userDuration;

	
	@FormField(filterable = false, type = FieldType.FIELD_TYPE_CHECKBOX)
	@Setter(value = AccessLevel.NONE)
	@Getter(value = AccessLevel.NONE)
	private boolean quizRepeatable;

	@FormField
	private Double score;

	@FormField(type = FieldType.FIELD_TYPE_DATETIME)
	@Getter(value = AccessLevel.NONE)
	private Date created;
	@FormField(type = FieldType.FIELD_TYPE_DATETIME) 
	private Date updated;
	@FormField(type = FieldType.FIELD_TYPE_DATETIME) 
	private Date modifiedDate;

	private int remainingDuration;
	private Integer maxQuestionNumber;

	/**
	 * required when updating answer via websocket
	 */
	private String token, requestId;

	public boolean isQuizRepeatable() {
		if (null != quiz)
			return quiz.isRepeatable();
		return quizRepeatable;
	}

	public Date getCreated() {
		return getCreatedDate();
	}

 

	public String getQuizDuration() {
		if (null != quiz)
			return DateUtil.timerString(quiz.getDuration());
		return quizDuration;
	}

	public String getUserDuration() {
		if (null == started || null == ended)
			return "-";
		if (started.after(ended)) {
			// invalid
			return "invalid";
		}
		long seconds = (ended.getTime() - started.getTime()) / 1000;
		return DateUtil.timerString(seconds);
	}

}
