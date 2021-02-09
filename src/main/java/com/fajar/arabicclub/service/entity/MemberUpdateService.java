package com.fajar.arabicclub.service.entity;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.fajar.arabicclub.constants.AuthorityType;
import com.fajar.arabicclub.entity.Authority;
import com.fajar.arabicclub.entity.User;
import com.fajar.arabicclub.exception.ApplicationException;
import com.fajar.arabicclub.repository.AuthorityRepository;
import com.fajar.arabicclub.service.SessionValidationService;

@Service
public class MemberUpdateService extends BaseEntityUpdateService<User>{

	@Autowired
	private AuthorityRepository authorityRepository;
	@Autowired
	private SessionValidationService sessionValidationService;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Override
	public User saveEntity(User object, boolean newRecord, HttpServletRequest httpServletRequest) throws Exception {
		 
		User loggedUser = sessionValidationService.getLoggedUser(httpServletRequest);
		if (!loggedUser.isAdmin()) {
			throw new ApplicationException("Not allowed");
		}
		validateEntityFields(object, newRecord, httpServletRequest);
		String encodedPassword = passwordEncoder.encode(object.getPassword());
		if (newRecord) {
			object.setPassword(encodedPassword);
			object.setAuthorities(getMemberAuthorities(object.getMainRole()));
		} else {
			User existingObject = entityRepository.findById(User.class, object.getId());
			if (existingObject.getPassword().equals(object.getPassword())) {
				//password not updated
			} else {
				object.setPassword(encodedPassword);
			}
			Set<Authority> memberRoles = getMemberAuthorities(object.getMainRole());
			if (existingObject.hasAuthority(object.getMainRole())) {
				object.setAuthorities(existingObject.getAuthorities());
			} else {
				memberRoles.add(authorityRepository.findTop1ByName(object.getMainRole()));
				object.setAuthorities(memberRoles);
			}
			 
		}
		
		return entityRepository.save(object);
	}
	
	
	private Set<Authority> getMemberAuthorities(AuthorityType type) {
		Authority roleUser = authorityRepository.findTop1ByName(type);
		Set<Authority> authorities = new HashSet<>();
		authorities.add(roleUser);
		return authorities ;
	}
}
