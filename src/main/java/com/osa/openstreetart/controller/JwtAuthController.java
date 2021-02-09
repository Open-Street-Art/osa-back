package com.osa.openstreetart.controller;

import com.osa.openstreetart.dto.UserRegisterDto;
import com.osa.openstreetart.entity.UserEntity;
import com.osa.openstreetart.repository.UserRepository;
import com.osa.openstreetart.server.JwtResponse;
import com.osa.openstreetart.server.JwtRequest;
import com.osa.openstreetart.server.JwtResponse;
import com.osa.openstreetart.service.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class JwtAuthController {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private JwtService jwtService;

	//@Autowired
	//private AuthenticationManager authManager;

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<?> postRegister(@RequestBody UserRegisterDto user) throws Exception {
		if (userRepo.findByEmail(user.getEmail()) != null) {
			return new ResponseEntity<>("E-mail already existing.", HttpStatus.BAD_REQUEST);
		}
		if (userRepo.findByUsername(user.getUsername()) != null) {
			return new ResponseEntity<>("User already existing.", HttpStatus.BAD_REQUEST);
		}
		if (user.getUsername().length < 4) {
			return new ResponseEntity<>("Username too short.", HttpStatus.BAD_REQUEST);
		}
		if (user.getPassword().length() < 8) {
			return new ResponseEntity<>("Password too short.", HttpStatus.BAD_REQUEST);
		}
		// if (user.getRoles().length <= 0 ) {
		// return new ResponseEntity<>("Role not define.", HttpStatus.BAD_REQUEST);
		// }
		// if (user.getRoles().length <= 0 && user.getRoles().length > 2) {
		// return new ResponseEntity<>("Shoose User or Artist.",
		// HttpStatus.BAD_REQUEST);
		// }
		return ResponseEntity.ok(jwtService.save(user));
	}

	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> postAuthenticate(@RequestBody JwtRequest request) throws Exception {
		try {
			authManager
					.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
		final UserDetails userDetails =
		 jwtService.loadUserByUsername(request.getEmail());
		final String token = jwtUtil.generateToken(userDetails);
		return ResponseEntity.ok(new JwtResponse(token));
	}
}
