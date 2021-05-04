package com.osa.openstreetart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.osa.openstreetart.dto.ChangeMailDTO;
import com.osa.openstreetart.dto.ChangePswDTO;
import com.osa.openstreetart.dto.UserPatchProfileDTO;
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
class UserControllerTest {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private TestUtil testUtil;

	@Autowired
	private PasswordEncoder bcryptEncoder;

	@Test
	void patchUserEmailTest() throws Exception {
		testUtil.cleanDB();

		// Creation d'un utilisateur
		UserEntity user = testUtil.createUser();
		userRepo.save(user);

		// Génération d'un token JWT pour utiliser la route
		String token = testUtil.getJWTwithUsername(user.getUsername());

		// Création du DTO
		ChangeMailDTO dto = new ChangeMailDTO();
		dto.setNewMail("newmail@mail.fr");

		// Changement de l'email
		mvc.perform(patch("/api/user/email").header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtil.asJsonString(dto)))
				.andExpect(status().isOk());

		// Verificatiom de la modification
		Optional<UserEntity> optionalUser = userRepo.findByEmail("newmail@mail.fr");
		assertEquals("tester", optionalUser.get().getUsername());
	}

	@Test
	void patchUserPasswordTest() throws Exception {
		testUtil.cleanDB();

		// Creation d'un utilisateur
		UserEntity user = testUtil.createUser();
		userRepo.save(user);

		// Génération d'un token JWT pour utiliser la route
		String token = testUtil.getJWTwithUsername(user.getUsername());

		// Création des DTO
		ChangePswDTO dto = new ChangePswDTO();
		dto.setOldPassword("psw123");
		dto.setNewPassword("psw456789");

		// Changement du mot de passe
		mvc.perform(patch("/api/user/password").header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtil.asJsonString(dto)))
				.andExpect(status().isOk());

		// Verificatiom de la modification
		Optional<UserEntity> optUser = userRepo.findByEmail(user.getEmail());
		assertEquals(true, bcryptEncoder.matches("psw456789", optUser.get().getPassword()));
	}

	@Test
	void getUserProfileTest() throws Exception {
		// Creation d'un utilisateur
		UserEntity user = testUtil.createUser();
		user = userRepo.save(user);

		MvcResult res = mvc.perform(get("/api/user/" + user.getId())).andExpect(status().isOk()).andReturn();

		System.out.println(res.getResponse().getContentAsString());

		assertEquals(true,
				res.getResponse().getContentAsString().contains("\"username\":\"tester\""));
	}

	@Test
	void patchUserProfileTest() throws JsonProcessingException, Exception {
		testUtil.cleanDB();

		// Creation d'un utilisateur
		UserEntity user = testUtil.createUser();
		user = userRepo.save(user);

		// Génération d'un token JWT pour utiliser la route
		String token = testUtil.getJWTwithUsername(user.getUsername());

		// Creation du formulaire de modification du profil
		UserPatchProfileDTO dto = new UserPatchProfileDTO();
		dto.setIsPublic(true);
		dto.setDescription("Nouvelle description");
		dto.setProfilePicture("1234");

		// Changement du profil
		mvc.perform(patch("/api/user/profile")
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON)
			.content(testUtil.asJsonString(dto)))
			.andExpect(status().isOk()).andReturn();

		assertEquals(true, userRepo.findByUsername(user.getUsername()).get().getIsPublic());
		assertEquals("Nouvelle description", userRepo.findByUsername(user.getUsername()).get().getDescription());
	}

	@Test
	void getUserProfileWithJWTTest() throws Exception {
		testUtil.cleanDB();

		// Creation d'un utilisateur
		UserEntity user = testUtil.createUser();
		user = userRepo.save(user);

		// Génération d'un token JWT pour utiliser la route
		String token = testUtil.getJWTwithUsername(user.getUsername());

		// Changement du profil
		MvcResult res = mvc.perform(get("/api/user/profile")
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
			.andExpect(status().isOk()).andReturn();

		System.out.println(res.getResponse().getContentAsString());

		assertEquals(true,
			res.getResponse().getContentAsString().contains("\"username\":\"tester\""));
	}

}
