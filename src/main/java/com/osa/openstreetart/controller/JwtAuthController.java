package com.osa.openstreetart.controller;

import com.osa.openstreetart.dto.UserLoginDto;
import com.osa.openstreetart.dto.UserRegisterDto;
import com.osa.openstreetart.entity.UserEntity;
import com.osa.openstreetart.repository.UserRepository;
import com.osa.openstreetart.service.JwtService;
import com.osa.openstreetart.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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
	private UserRepository userRepository;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private JwtUtil jwtutil;

	@Autowired
	private AuthenticationManager authManager;

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<?> postRegister(@RequestBody UserRegisterDto user) throws Exception {
		if (userRepository.findByEmail(user.getEmail()) != null) {
			return new ResponseEntity<>("E-mail already existing.", HttpStatus.BAD_REQUEST);
		}
		if (userRepository.findByUsername(user.getUsername()) != null) {
			return new ResponseEntity<>("Username already existing.", HttpStatus.BAD_REQUEST);
		}

		// Question? dois-il y avoir une constrainte sur le nombre de caractère du username
		if (user.getUsername().length() < 4) {
			return new ResponseEntity<>("Username too short.", HttpStatus.BAD_REQUEST);
		}

		if (user.getPassword().length() < UserEntity.PSW_MIN_LENGTH) {
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
	public ResponseEntity<?> postAuthenticate(@RequestBody UserLoginDto request) throws Exception {
		try {
			authManager
					.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}

		final UserDetails userDetails = jwtService.loadUserByUsername(request.getUsername());
		final String token = jwtutil.generateToken(userDetails);

		return ResponseEntity.ok(new Object() { final String jwtToken = token;});
	}
}
