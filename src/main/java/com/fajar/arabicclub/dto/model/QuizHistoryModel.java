package com.fajar.arabicclub.dto.model;

import java.util.Date;

import com.fajar.arabicclub.annotation.Dto;
import com.fajar.arabicclub.annotation.FormField;
import com.fajar.arabicclub.constants.FieldType;
import com.fajar.arabicclub.entity.QuizHistory;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
@Dto(entityClass = QuizHistory.class, editable = false)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizHistoryModel extends BaseModel<QuizHistory>  {

	 /**
	 * 
	 */
	private static final long serialVersionUID = 4503414813358642804L;
	@FormField(optionItemName = "displayName") 
	private UserModel user; 
	
	@FormField(type=FieldType.FIELD_TYPE_DATETIME)
	private Date started;
	@FormField(type=FieldType.FIELD_TYPE_DATETIME)
	private Date ended;
	
	@FormField(optionItemName = "title")
	private QuizModel quiz; 
	@FormField(filterable = false, type=FieldType. FIELD_TYPE_CHECKBOX)
	@Setter(value = AccessLevel.NONE)
	@Getter(value = AccessLevel.NONE)
	private boolean quizRepeatable;
	
	@FormField
	private Double score;
	
	
	@FormField(type=FieldType.FIELD_TYPE_DATETIME)
	@Getter(value=AccessLevel.NONE)
	private Date created;
	@FormField(type=FieldType.FIELD_TYPE_DATETIME)
	@Getter(value=AccessLevel.NONE)
	private Date updated;
	
	public boolean isQuizRepeatable() {
		if (null != quiz) return quiz.isRepeatable();
		return quizRepeatable;
	}
	
	public Date getCreated() {
		return getCreatedDate() ;
	}
	public Date getUpdated() {
		return getModifiedDate();
	}
	 

}
