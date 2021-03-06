package com.osa.openstreetart.controller;

import com.osa.openstreetart.dto.ChangeMailDTO;
import com.osa.openstreetart.dto.ChangePswDTO;
import com.osa.openstreetart.dto.OSAResponseDTO;
import com.osa.openstreetart.dto.UserPatchProfileDTO;
import com.osa.openstreetart.entity.UserEntity;
import com.osa.openstreetart.exceptions.OSA400Exception;
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

import lombok.extern.slf4j.Slf4j;

@ApiRestController
@Slf4j
public class UserController {
	
	@Autowired
	UserService userService;

	@Autowired
	JwtUtil jwtUtil;

	private static final String  TOKEN_PREFIX = "Bearer ";

	@GetMapping(value = "/user/{user_id}")
	public ResponseEntity<OSAResponseDTO> getUserProfile(
			@PathVariable("user_id") Integer userId) throws OSA400Exception {
		
		log.info("Served User : " + userId + " profile");
		return ResponseEntity.ok(
			new OSAResponseDTO(
				userService.loadUserProfileDTO(userService.getOrFail(userId))
			)
		);
	}

	@PatchMapping(value = "/user/email")
	public ResponseEntity<OSAResponseDTO> patchUserEmail(@RequestHeader(value = "Authorization") String token, 
			@RequestBody ChangeMailDTO dto) throws OSA400Exception {
		String username = jwtUtil.getUsernameFromToken(token.substring(TOKEN_PREFIX.length()));
		
		UserEntity user = userService.getOrFail(username);
		userService.changerUserMail(user, dto.getNewMail());

		log.info("User: " + user.getId() + " email modified");
		return ResponseEntity.ok(new OSAResponseDTO("Email modified."));
	}
	
	@PatchMapping(value = "/user/password")
	public ResponseEntity<OSAResponseDTO> patchUserPassword(@RequestHeader(value = "Authorization") String token,
			@RequestBody ChangePswDTO dto) throws OSA400Exception {
		
		String username = jwtUtil.getUsernameFromToken(token.substring(TOKEN_PREFIX.length()));
		
		UserEntity user = userService.getOrFail(username);

		userService.changeUserPassword(user, dto.getOldPassword(), dto.getNewPassword());
		
		log.info("User: " + user.getId() + " password modified");
		return ResponseEntity.ok(new OSAResponseDTO("Password modified."));
	}

	@PatchMapping(value = "/user/profile")
	public ResponseEntity<OSAResponseDTO> patchUserProfile(@RequestHeader(value = "Authorization") String token,
			@RequestBody UserPatchProfileDTO dto) throws OSA400Exception {
		String username = jwtUtil.getUsernameFromToken(token.substring(TOKEN_PREFIX.length()));
		
		UserEntity user = userService.getOrFail(username);
		userService.patchUser(user, dto);
		userService.save(user);
		
		log.info("User : " + user.getId() + " profile modified");
		return ResponseEntity.ok(new OSAResponseDTO("Profile modified."));
	}

	@GetMapping(value = "/user/profile")
	public ResponseEntity<OSAResponseDTO> getUserProfile(
			@RequestHeader(value = "Authorization") String token) throws OSA400Exception {

		String username = jwtUtil.getUsernameFromToken(token.substring(TOKEN_PREFIX.length()));
		
		UserEntity user = userService.getOrFail(username);
		log.info("User : " + user.getId() + " profile Served");

		return ResponseEntity.ok(new OSAResponseDTO(userService.loadUserProfileDTO(user)));
	}
}
