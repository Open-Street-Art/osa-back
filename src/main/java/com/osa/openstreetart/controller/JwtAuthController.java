package com.osa.openstreetart.controller;

import com.osa.openstreetart.dto.JwtDTO;
import com.osa.openstreetart.dto.UserLoginDTO;
import com.osa.openstreetart.dto.UserRegisterDTO;
import com.osa.openstreetart.exceptions.OSA400Exception;
import com.osa.openstreetart.exceptions.OSA409Exception;
import com.osa.openstreetart.service.UserService;
import com.osa.openstreetart.util.ApiRestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@ApiRestController
public class JwtAuthController {

	@Autowired
	private UserService userService;

	@ApiOperation(value = "Création d'un nouveau compte OpenStreetArts.", 
		notes = "API assurant la création d’un utilisateur au sein de la base de données.")
	@ApiResponses(value = {
		@ApiResponse(code = 201, message = "Nouveau compte crée avec succès."),
		@ApiResponse(code = 400, message = "Echec de la création du nouveau compte."),
		@ApiResponse(code = 409, message = "Le nom d'utilisateur est déjà pris.")
	})
	@PostMapping(value = "/register")
	public ResponseEntity<String> postRegister(@RequestBody UserRegisterDTO user)
			throws OSA409Exception, OSA400Exception {
		userService.register(user);
		return ResponseEntity.ok("User registered.");
	}

	
	@ApiOperation(value = "Authentification d'un utilisateur.", 
		notes = "API assurant l’authentification d’un utilisateur en lui renvoyant un token JWT valide 5 jours utilisable sur les routes de l’API.",
		response = JwtDTO.class)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Authentification réussi."),
		@ApiResponse(code = 400, message = "Echec de l'authentification.")
	})
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", example = "Bearer access_token")
	@PostMapping(value = "/authenticate")
	public ResponseEntity<JwtDTO> postAuthenticate(@RequestBody UserLoginDTO request) {
		final String token = userService.login(request);
		return ResponseEntity.ok(new JwtDTO(token));
	}
	
}
