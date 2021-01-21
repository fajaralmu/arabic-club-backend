package com.fajar.arabicclub.service.entity;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.fajar.arabicclub.dto.WebResponse;
import com.fajar.arabicclub.entity.User;

@Service
public class UserUpdateService extends BaseEntityUpdateService<User>{
 
	
	@Override
	public WebResponse saveEntity(User baseEntity, boolean newRecord, HttpServletRequest httoHttpServletRequest) {
		try {
			User user = (User) copyNewElement(baseEntity, newRecord);
			this.validateEntityFields(user, newRecord);
			User newUser = entityRepository.save(user);
			return WebResponse.builder().success(true).entity(newUser).build();
		}catch (Exception e) {
			return WebResponse.builder().success(false).entity(null).build();
		}
	}
}
