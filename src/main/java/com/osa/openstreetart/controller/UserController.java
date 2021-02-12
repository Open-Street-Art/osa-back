package com.osa.openstreetart.controller;

import java.util.Optional;

import com.osa.openstreetart.dto.UserProfileDTO;
import com.osa.openstreetart.entity.UserEntity;
import com.osa.openstreetart.exceptions.OSA400Exception;
import com.osa.openstreetart.repository.UserRepository;
import com.osa.openstreetart.service.UserService;
import com.osa.openstreetart.util.ApiRestController;
import com.osa.openstreetart.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@ApiRestController
public class UserController {
	
	@Autowired
	UserService userService;

	@Autowired
	JwtUtil jwtUtil;

	@Autowired
	UserRepository userRepo;

	@GetMapping(value = "/user/{user_id}")
	public ResponseEntity<UserProfileDTO> getUserProfile(
			@PathVariable("user_id") Integer userId) throws OSA400Exception {
		Optional<UserEntity> optUser = userRepo.findById(userId);
		if (!optUser.isPresent())
			throw new OSA400Exception("User not found.");

		return ResponseEntity.ok(userService.loadUserProfileDTO(optUser.get()));
	}

	@PatchMapping(value = "/user/email")
	public ResponseEntity<String> patchUserEmail(@RequestHeader(value="Authorization") String token, 
			@RequestBody String newMail) throws OSA400Exception {
		String username = jwtUtil.getUsernameFromToken(token.substring("Bearer ".length()));
		Optional<UserEntity> optionalUser = userRepo.findByUsername(username);
		
		if (!optionalUser.isPresent())
			throw new OSA400Exception("No user found.");

		if (!userService.isValidEmailAddress(newMail))
			throw new OSA400Exception("Invalid email address.");

		optionalUser.get().setEmail(newMail);
		userRepo.save(optionalUser.get());
		
		return ResponseEntity.ok("Email modified.");
	}

}
