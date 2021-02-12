package com.osa.openstreetart;

import com.osa.openstreetart.entity.UserEntity;
import com.osa.openstreetart.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
	
	@Autowired
	private UserRepository userRepo;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private TestUtil testUtil;

	@Autowired
	private PasswordEncoder bcryptEncoder;

	@Test
	public void patchUserEmailTest() throws Exception {
		testUtil.cleanDB();

		// Creation d'un utilisateur
		UserEntity user = testUtil.createUser();
		userRepo.save(user);

		// Génération d'un token JWT pour utiliser la route
		String token = testUtil.getJWTwithUsername(user.getUsername());

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

	@Test
	public void patchUserPasswordTest() throws Exception {
		testUtil.cleanDB();

		// Creation d'un utilisateur
		UserEntity user = testUtil.createUser();
		userRepo.save(user);

		// Génération d'un token JWT pour utiliser la route
		String token = testUtil.getJWTwithUsername(user.getUsername());

		// Changement de l'email
		mvc.perform(patch("/api/user/password")
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON)
			.content("UnNouveauPass123"))
			.andExpect(status().isOk());


		// Fausse tentative
		mvc.perform(patch("/api/user/email")
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON)
			.content("unmauvaispass"))
			.andExpect(status().isBadRequest());

		// Verificatiom de la modification
		Optional<UserEntity> optUser = userRepo.findByEmail(user.getEmail());
		System.out.println("DEBUG" + optUser.get().getPassword());
		assertEquals(
			true, 
			bcryptEncoder.matches("UnNouveauPass123", optUser.get().getPassword())
		);
	}

	@Test
	public void getUserProfileTest() throws Exception {
		// Creation d'un utilisateur
		UserEntity user = testUtil.createUser();
		user = userRepo.save(user);

		MvcResult res = mvc.perform(get("/api/user/" + user.getId()))
			.andExpect(status().isOk()).andReturn();

		System.out.println(res.getResponse().getContentAsString());
		assertEquals(
			true,
			res.getResponse()
				.getContentAsString()
				.contains("\"username\":\"tester\",\"roles\":\"[ROLE_USER]\"")
		);
	}

}
