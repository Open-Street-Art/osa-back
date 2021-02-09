package com.osa.openstreetart;

import com.osa.openstreetart.entity.UserEntity;
import com.osa.openstreetart.repository.UserRepository;
import com.osa.openstreetart.service.JwtService;
import com.osa.openstreetart.util.JwtUtil;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
	
	@Autowired
	UserRepository userRepo;

	@Autowired
	MockMvc mvc;

	@Autowired
	JwtService jwtService;

	@Autowired
	JwtUtil jwtUtil;

	@Test
	public void patchUserEmailTest() throws Exception {
		// Creation d'un utilisateur
		UserEntity user = new UserEntity();
		user.setEmail("test@mail.fr");
		user.setUsername("tester");
		user.setPassword("psw123");
		userRepo.save(user);

		// Génération d'un token JWT pour utiliser la route
		UserDetails userDetails = jwtService.loadUserByUsername(user.getEmail());
		String token = jwtUtil.generateToken(userDetails);

		// Changement de l'email
		mvc.perform(patch("/api/user/email")
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON)
			.content("test@mail.fr"))
			.andExpect(status().isOk());

		// Fausse tentative
		mvc.perform(patch("/api/user/email")
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON)
			.content("testmail.fr"))
			.andExpect(status().isBadRequest());

		// Verificatiom de la modification
		Optional<UserEntity> optionalUser = userRepo.findByEmail("test@mail.fr");
		assertEquals(optionalUser.get().getUsername(), "tester");
	}

}
