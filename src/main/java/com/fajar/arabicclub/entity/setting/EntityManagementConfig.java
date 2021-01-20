package com.fajar.arabicclub.entity.setting;

import java.io.Serializable;

import com.fajar.arabicclub.annotation.Dto;
import com.fajar.arabicclub.entity.BaseEntity;
import com.fajar.arabicclub.service.entity.BaseEntityUpdateService;
import com.fajar.arabicclub.util.EntityUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntityManagementConfig implements Serializable {
	private static final long serialVersionUID = -3980738115751592524L;
	private long id;
	@JsonIgnore
	private Class<? extends BaseEntity> entityClass;
	@JsonIgnore
	private BaseEntityUpdateService entityUpdateService;
	@JsonIgnore
	private String fieldName;
	private boolean disabled;
	private String iconClassName;
	
	public EntityManagementConfig setIconClassName(String iconClassName) {
		this.iconClassName = iconClassName;
		return this;
	}

	public EntityManagementConfig(String fieldName, Class<? extends BaseEntity> entityClass,
			BaseEntityUpdateService service, EntityUpdateInterceptor updateInterceptor) {
		this.entityClass = entityClass;
		this.entityUpdateService = service;
		if (null == fieldName) {
			fieldName = "entity";
		}
		this.fieldName = fieldName;
//		this.updateInterceptor = updateInterceptor;
		init();
	}

	private void init() {
		Dto dtoAnnotation = EntityUtil.getClassAnnotation(entityClass, Dto.class);

		disabled = dtoAnnotation.editable() == false;
	}

	public String getLabel() {
		Dto dtoAnnotation = EntityUtil.getClassAnnotation(entityClass, Dto.class);

		String label = dtoAnnotation.value().equals("") ? entityClass.getSimpleName() : dtoAnnotation.value();
		return label;
	}

	public String getEntityName() {
		return entityClass.getSimpleName().toLowerCase();
	}

}
