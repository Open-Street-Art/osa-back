package com.osa.openstreetart.controller;

import java.util.Optional;

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

	private static String tokenPrefix = "Bearer ";
	private static String userNotFoundMsg = "User not found.";

	@GetMapping(value = "/user/{user_id}")
	public ResponseEntity<OSAResponseDTO> getUserProfile(
			@PathVariable("user_id") Integer userId) throws OSA400Exception {
		Optional<UserEntity> optUser = userRepo.findById(userId);
		if (!optUser.isPresent())
			throw new OSA400Exception(userNotFoundMsg);

		return ResponseEntity.ok(
			new OSAResponseDTO(
				userService.loadUserProfileDTO(optUser.get())
			)
		);
	}

	@PatchMapping(value = "/user/email")
	public ResponseEntity<OSAResponseDTO> patchUserEmail(@RequestHeader(value = "Authorization") String token, 
			@RequestBody String newMail) throws OSA400Exception {
		String username = jwtUtil.getUsernameFromToken(token.substring(tokenPrefix.length()));
		Optional<UserEntity> optionalUser = userRepo.findByUsername(username);
		
		if (!optionalUser.isPresent())
			throw new OSA400Exception(userNotFoundMsg);

		if (!userService.isValidEmailAddress(newMail))
			throw new OSA400Exception("Invalid email address.");

		optionalUser.get().setEmail(newMail);
		userRepo.save(optionalUser.get());
		
		return ResponseEntity.ok(new OSAResponseDTO("Email modified."));
	}

	@PatchMapping(value = "/user/password")
	public ResponseEntity<OSAResponseDTO> patchUserPassword(@RequestHeader(value = "Authorization") String token,
			@RequestBody String newPassword) throws OSA400Exception {
		
		String username = jwtUtil.getUsernameFromToken(token.substring(tokenPrefix.length()));
		Optional<UserEntity> optionalUser = userRepo.findByUsername(username);

		if (!optionalUser.isPresent())
			throw new OSA400Exception(userNotFoundMsg);

		if (newPassword.length() < UserEntity.PSW_MIN_LENGTH)
			throw new OSA400Exception("Invalid password.");

		userService.changeUserPassword(optionalUser.get(), newPassword);

		return ResponseEntity.ok(new OSAResponseDTO("Password modified."));
	}

	@PatchMapping(value = "/user/profile")
	public ResponseEntity<OSAResponseDTO> patchUserProfile(@RequestHeader(value = "Authorization") String token,
			@RequestBody UserPatchProfileDTO dto) throws OSA400Exception {
		String username = jwtUtil.getUsernameFromToken(token.substring(tokenPrefix.length()));
		Optional<UserEntity> optionalUser = userRepo.findByUsername(username);
		if (!optionalUser.isPresent())
			throw new OSA400Exception(userNotFoundMsg);

		userService.patchUser(optionalUser.get(), dto);
		userRepo.save(optionalUser.get());

		return ResponseEntity.ok(new OSAResponseDTO("Profile modified."));
	}

	@GetMapping(value = "/user/profile")
	public ResponseEntity<OSAResponseDTO> getUserProfile(
			@RequestHeader(value = "Authorization") String token) throws OSA400Exception {

		String username = jwtUtil.getUsernameFromToken(token.substring(tokenPrefix.length()));
		Optional<UserEntity> optionalUser = userRepo.findByUsername(username);
		if (!optionalUser.isPresent())
			throw new OSA400Exception(userNotFoundMsg);

		return ResponseEntity.ok(new OSAResponseDTO(userService.loadUserProfileDTO(optionalUser.get())));
	}
}
