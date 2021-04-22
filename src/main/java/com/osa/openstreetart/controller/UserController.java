package com.osa.openstreetart.controller;

import com.osa.openstreetart.dto.OSAResponseDTO;
import com.osa.openstreetart.dto.UserPatchProfileDTO;
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

	private static final String  TOKEN_PREFIX = "Bearer ";

	@GetMapping(value = "/user/{user_id}")
	public ResponseEntity<OSAResponseDTO> getUserProfile(
			@PathVariable("user_id") Integer userId) throws OSA400Exception {
		return ResponseEntity.ok(
			new OSAResponseDTO(
				userService.loadUserProfileDTO(userService.getOrFail(userId))
			)
		);
	}

	@PatchMapping(value = "/user/email")
	public ResponseEntity<OSAResponseDTO> patchUserEmail(@RequestHeader(value = "Authorization") String token, 
			@RequestBody String newMail) throws OSA400Exception {
		String username = jwtUtil.getUsernameFromToken(token.substring(TOKEN_PREFIX.length()));
		
		UserEntity user = userService.getOrFail(username);
		if (!userService.isValidEmailAddress(newMail))
			throw new OSA400Exception("Invalid email address.");

		user.setEmail(newMail);
		userService.save(user);
		
		return ResponseEntity.ok(new OSAResponseDTO("Email modified."));
	}

	@PatchMapping(value = "/user/password")
	public ResponseEntity<OSAResponseDTO> patchUserPassword(@RequestHeader(value = "Authorization") String token,
			@RequestBody String newPassword) throws OSA400Exception {
		
		String username = jwtUtil.getUsernameFromToken(token.substring(TOKEN_PREFIX.length()));
		
		UserEntity user = userService.getOrFail(username);
		if (newPassword.length() < UserEntity.PSW_MIN_LENGTH)
			throw new OSA400Exception("Invalid password.");

		userService.changeUserPassword(user, newPassword);

		return ResponseEntity.ok(new OSAResponseDTO("Password modified."));
	}

	@PatchMapping(value = "/user/profile")
	public ResponseEntity<OSAResponseDTO> patchUserProfile(@RequestHeader(value = "Authorization") String token,
			@RequestBody UserPatchProfileDTO dto) throws OSA400Exception {
		String username = jwtUtil.getUsernameFromToken(token.substring(TOKEN_PREFIX.length()));
		
		UserEntity user = userService.getOrFail(username);
		userService.patchUser(user, dto);
		userService.save(user);

		return ResponseEntity.ok(new OSAResponseDTO("Profile modified."));
	}

	@GetMapping(value = "/user/profile")
	public ResponseEntity<OSAResponseDTO> getUserProfile(
			@RequestHeader(value = "Authorization") String token) throws OSA400Exception {

		String username = jwtUtil.getUsernameFromToken(token.substring(TOKEN_PREFIX.length()));
		
		UserEntity user = userService.getOrFail(username);

		return ResponseEntity.ok(new OSAResponseDTO(userService.loadUserProfileDTO(user)));
	}
}
