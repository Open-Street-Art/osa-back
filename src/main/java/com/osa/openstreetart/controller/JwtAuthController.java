package com.osa.openstreetart.controller;

import com.osa.openstreetart.dto.JwtDTO;
import com.osa.openstreetart.dto.UserLoginDTO;
import com.osa.openstreetart.dto.UserRegisterDTO;
import com.osa.openstreetart.service.UserService;
import com.osa.openstreetart.util.ApiRestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@ApiRestController
public class JwtAuthController {

	@Autowired
	private UserService userService;

	@PostMapping(value = "/register")
	public ResponseEntity<String> postRegister(@RequestBody UserRegisterDTO user) throws Exception {
		userService.register(user);
		return ResponseEntity.ok("User registered.");
	}

	@PostMapping(value = "/authenticate")
	public ResponseEntity<JwtDTO> postAuthenticate(@RequestBody UserLoginDTO request) throws Exception {
		final String token = userService.login(request);
		return ResponseEntity.ok(new JwtDTO(token));
	}
	
}
