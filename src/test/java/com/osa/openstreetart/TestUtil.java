package com.osa.openstreetart;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osa.openstreetart.entity.RoleEnum;
import com.osa.openstreetart.entity.UserEntity;
import com.osa.openstreetart.repository.ArtRepository;
import com.osa.openstreetart.repository.UserRepository;
import com.osa.openstreetart.service.JwtService;
import com.osa.openstreetart.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class TestUtil {

	@Autowired
	ArtRepository artRepo;

	@Autowired
	UserRepository userRepo;

	@Autowired
	JwtService jwtService;

	@Autowired
	JwtUtil jwtUtil;

	@Autowired
	private PasswordEncoder bcryptEncoder;

	public UserEntity createAdmin() {
		UserEntity admin = new UserEntity();
		admin.setEmail("admin@mail.fr");
		admin.setUsername("admin");
		admin.setPassword(bcryptEncoder.encode("psw123"));

		Collection<RoleEnum> roles = new ArrayList<RoleEnum>();
		roles.add(RoleEnum.ROLE_ADMIN);
		admin.setRoles(roles);

		return admin;
	}

	public UserEntity createUser() {
		UserEntity user = new UserEntity();
		user.setEmail("test@mail.fr");
		user.setUsername("tester");
		user.setIsPublic(false);
		user.setPassword(bcryptEncoder.encode("psw123"));

		Collection<RoleEnum> roles = new ArrayList<RoleEnum>();
		roles.add(RoleEnum.ROLE_USER);
		user.setRoles(roles);

		return user;
	}

	
	public String asJsonString(final Object obj) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }

	public String getJWTwithUsername(String username) {
		UserDetails userDetails = jwtService.loadUserByUsername(username);
		return jwtUtil.generateToken(userDetails);
	}

	public void cleanDB() {
		artRepo.deleteAll();
		userRepo.deleteAll();
	}

}
