package com.fajar.arabicclub.dto.model;

import com.fajar.arabicclub.annotation.Dto;
import com.fajar.arabicclub.constants.AuthorityType;
import com.fajar.arabicclub.entity.Authority;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Dto(entityClass = Authority.class)
public class AuthorityModel extends BaseModel<Authority> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2534190215509155334L;

	private AuthorityType name;

	@Override
	public Authority toEntity() {
		Authority entity = new Authority(); 
		copy(entity);
		return entity;
	}

	 
}
