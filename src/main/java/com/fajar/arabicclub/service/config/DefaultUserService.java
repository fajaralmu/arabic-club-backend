package com.fajar.arabicclub.service.config;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fajar.arabicclub.constants.AuthorityType;
import com.fajar.arabicclub.dto.WebRequest;
import com.fajar.arabicclub.dto.WebResponse;
import com.fajar.arabicclub.entity.Authority;
import com.fajar.arabicclub.entity.User;
import com.fajar.arabicclub.exception.ApplicationException;
import com.fajar.arabicclub.repository.AuthorityRepository;
import com.fajar.arabicclub.repository.EntityRepository;
import com.fajar.arabicclub.repository.UserRepository;
import com.fajar.arabicclub.service.SessionValidationService;
import com.fajar.arabicclub.service.resources.ImageUploadService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultUserService {
	@Autowired
	private UserRepository userRepository;  
	@Autowired
	private AuthorityRepository authorityRepository;
	@Autowired
	private SessionValidationService sessionValidationService;  
	@Autowired
	private EntityRepository entityRepository;
	
	@Autowired
	private ImageUploadService imageUploadService;
	
	
	private BCryptPasswordEncoder passwordEncoder;
	
	public void setPasswordEncoder(BCryptPasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}
	
	@PostConstruct
	public void init() {
		try {
			checkUserAuthorities();
			checkUser();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * don't invoke this method when tables has not been created yet
	 */
	private void checkUserAuthorities() {
		Authority auth = authorityRepository.findTop1ByName(AuthorityType.ROLE_ADMIN);
		if (null == auth) {
			auth = Authority.createAdmin();
			authorityRepository.save(auth);
		}
		Authority authUser = authorityRepository.findTop1ByName(AuthorityType.ROLE_USER);
		if (null == authUser) {
			authUser = Authority.createUser();
			authorityRepository.save(authUser);
		}
	}

	/**
	 * don't invoke this method when tables has not been created yet
	 */
	private void checkUser() {
		log.info("___________checkUser");
		List<User> adminUser = userRepository.getByAuthority(AuthorityType.ROLE_ADMIN.toString());
		if (adminUser == null || adminUser.isEmpty()) {
			generateDefaultAdmin();
		}
	}
	
	private void generateDefaultAdmin() {
		Authority adminAuth = authorityRepository.findTop1ByName(AuthorityType.ROLE_ADMIN);
		if (null == adminAuth) {
			log.info("___________null == adminAuth");
			return;
		}
		
		User user = new User();
		Authority auth = new Authority();
		auth.setId(adminAuth.getId());
		user.addAuthority(auth );
		user.setPassword(passwordEncoder.encode("123"));
		user.setUsername("admin");
		user.setDisplayName("Application Admin");
		
		log.info("___________userRepository.save(user)");
		userRepository.save(user);
	}
	
	public WebResponse updateProfile(HttpServletRequest httpServletRequest, WebRequest webRequest) {
		log.info("Update profile");
		
		final User loggedUser = sessionValidationService.getLoggedUser(httpServletRequest);
		final User user = webRequest.getUser().toEntity();
		 
		updateUserData(loggedUser, user, httpServletRequest);
		
		WebResponse response = new WebResponse();
		response.setUser(loggedUser.toModel());
		return response;
	}

	private void updateUserData(User loggedUser, User user, HttpServletRequest httpServletRequest) {
		if (user.getUsername() != null && !user.getUsername().isEmpty()) {
			loggedUser.setUsername(user.getUsername());
		}
		if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
			loggedUser.setDisplayName(user.getDisplayName());
		}
		if (user.getPassword() != null && !user.getPassword().isEmpty()) {
			loggedUser.setPassword(passwordEncoder.encode(user.getPassword()));
		}
		
		if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
			loggedUser.setProfileImage(user.getProfileImage());
			imageUploadService.upload(loggedUser, httpServletRequest);
		}
		entityRepository.save(loggedUser);
	}

	public WebResponse register(WebRequest webRequest) {
		try {
			User user = webRequest.getUser().toEntity();
			Authority authUser = authorityRepository.findTop1ByName(AuthorityType.ROLE_USER);
			user.addAuthority(authUser);
			user.encodePassword(passwordEncoder);
			userRepository.save(user);
			
			WebResponse response = new WebResponse();
			return response;

		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}
}
