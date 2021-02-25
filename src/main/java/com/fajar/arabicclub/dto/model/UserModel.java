package com.fajar.arabicclub.dto.model;

import java.util.HashSet;
import java.util.Set;

import com.fajar.arabicclub.annotation.Dto;
import com.fajar.arabicclub.annotation.FormField;
import com.fajar.arabicclub.constants.AuthorityType;
import com.fajar.arabicclub.constants.FieldType;
import com.fajar.arabicclub.entity.Authority;
import com.fajar.arabicclub.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Dto( updateService = "memberUpdateService")
@Builder
@Data
@AllArgsConstructor
@JsonInclude(value = Include.NON_NULL)
@NoArgsConstructor
public class UserModel extends BaseModel<User>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3896877759244837620L;
	@FormField
	private String username;
	@FormField
	private String displayName;
	@FormField(labelName = "Password (nullable)", required = false)
	@Setter(value = AccessLevel.NONE)
	private String editPassword;
	
	@FormField(type = FieldType.FIELD_TYPE_IMAGE)
	private String profileImage;
	
	/**
	 * for CRUD
	 */
	@FormField(type=FieldType.FIELD_TYPE_PLAIN_LIST) 
	@Setter(value=AccessLevel.NONE)
	@Getter(value=AccessLevel.NONE)
	private AuthorityType role;
	
	/**
	 * not exposed
	 */
	@JsonIgnore
	private String password;
	@JsonIgnore
	private AuthorityType mainRole;
	
	public void setEditPassword(String editPassword) {
		this.editPassword = editPassword;
		this.password = editPassword;
	}
	
	public AuthorityType getRole() {
		if (null != authorities && authorities.size() > 0) {
			return ((Authority)authorities.toArray()[0]).getName();
		}
		return role;
	}
	
	public void setRole(AuthorityType role) {
		this.role = role;
		this.mainRole = role;
	}

	@Default
	@JsonIgnore
	private Set<AuthorityModel> authorities = new HashSet<>();

	@JsonIgnore
	private String requestId; 

	@Override
	public User toEntity() {
		
		User user = super.toEntity();
		Set<Authority> _authorities = new HashSet<Authority>();
		this.authorities.forEach(a->{_authorities.add(a.toEntity());});
		user.setAuthorities(_authorities );
		return user;
	}
	
}
