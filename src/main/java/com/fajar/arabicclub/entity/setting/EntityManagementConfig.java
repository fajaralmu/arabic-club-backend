package com.fajar.arabicclub.entity.setting;

import java.io.Serializable;

import com.fajar.arabicclub.annotation.Dto;
import com.fajar.arabicclub.dto.model.BaseModel;
import com.fajar.arabicclub.entity.BaseEntity;
import com.fajar.arabicclub.exception.ApplicationException;
import com.fajar.arabicclub.service.entity.BaseEntityUpdateService;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

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
	@JsonIgnore
	@Default
	private Class<? extends BaseModel> modelClass = BaseModel.class;
	
	

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
	
	public EntityManagementConfig setIconClassName(String iconClassName) {
		this.iconClassName = iconClassName;
		return this;
	}

	private void init() {
//		CustomEntity customEntity = entityClass.getAnnotation(CustomEntity.class);
//		if (null == customEntity) {
//			throw new ApplicationException("NOT Custom Entity: "+ entityClass) ;
//		}
		modelClass = BaseEntity.getTypeArgumentOfGenericSuperClass(entityClass);
		
		Dto dtoAnnotation = modelClass.getAnnotation(Dto.class);
		if (null == dtoAnnotation) {
			throw new ApplicationException("NOT Custom Entity: "+ modelClass) ;
		}
		disabled = dtoAnnotation.editable() == false;
	}

	public String getLabel() {
		Dto dtoAnnotation = modelClass.getAnnotation(Dto.class);
		if (null == dtoAnnotation) {
			throw new ApplicationException("NOT Custom Entity: "+ modelClass) ;
		}
		String label = dtoAnnotation.value().equals("") ? entityClass.getSimpleName() : dtoAnnotation.value();
		return label;
	}

	public String getEntityName() {
		return entityClass.getSimpleName().toLowerCase();
	}

}
