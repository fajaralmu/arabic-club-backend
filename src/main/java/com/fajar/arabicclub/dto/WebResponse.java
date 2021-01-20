package com.fajar.arabicclub.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fajar.arabicclub.annotation.Dto;
import com.fajar.arabicclub.entity.ApplicationProfile;
import com.fajar.arabicclub.entity.BaseEntity;
import com.fajar.arabicclub.entity.User;
import com.fajar.arabicclub.entity.setting.EntityProperty;
import com.fajar.arabicclub.util.CollectionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Dto
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class WebResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8345271799535134609L;
	@Builder.Default
	private Date date = new Date();
	private User user;
	@Builder.Default
	private String code = "00";
	@Builder.Default
	private String message = "success";
	@Builder.Default
	@Setter(value = AccessLevel.NONE)
	private List<BaseEntity> entities = new ArrayList<>();
	
	private List<?> generalList;
	
	private BaseEntity entity;
	private Filter filter;
	private Integer totalData; 
	private Map<String, Object> storage;
	private EntityProperty entityProperty;
	
	private Long maxValue;
	private Integer quantity;
	private SessionData sessionData;
	private ApplicationProfile applicationProfile;

	private double percentage;
	private Integer[] transactionYears;
	 
	private String requestId; 
	private String token;
 
	private Boolean loggedIn;

	@Builder.Default
	@JsonIgnore
	private boolean success = true;
	@JsonIgnore
	private Class<? extends BaseEntity> entityClass; 
	
	public WebResponse(String code, String message) {
		this.code = code;
		this.message = message;
		this.date = new Date();
	}

	public <T extends BaseEntity> void setEntities(List<T > entities) {
		this.entities = CollectionUtil.convertList(entities);
	}
	
	public static WebResponse failedResponse() {
		return new WebResponse("01", "INVALID REQUEST");
	}

	

	public static WebResponse failed() {
		return failed("INVALID REQUEST");
	}

	public static WebResponse failed(Exception e) {
		return failed(e.getMessage());
	}

	public static WebResponse failed(String msg) {
		return new WebResponse("01", msg);
	}

	public static WebResponse success() {
		return new WebResponse("00", "SUCCESS");
	}

	public static WebResponse invalidSession() {
		return new WebResponse("02", "Invalid Session");
	}

	
	
	
}
