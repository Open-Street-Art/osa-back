package com.osa.openstreetart;

import com.osa.openstreetart.entity.RoleEnum;
import com.osa.openstreetart.entity.UserEntity;
import com.osa.openstreetart.dto.UserLoginDTO;
import com.osa.openstreetart.dto.UserRegisterDTO;
import com.osa.openstreetart.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class JwtAuthControllerTest {

    @Autowired
	private UserRepository userRepo;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private TestUtil testUtil;
	
	@Test
	public void postAuthenticateTest() throws Exception {
        testUtil.cleanDB();

		// Creation d'un utilisateur
		UserEntity user = testUtil.createUser();
		userRepo.save(user);

		// Creation du l'objet de authentification : username, paswword
        UserLoginDTO userCred = new UserLoginDTO();
        userCred.setUsername("tester");
        userCred.setPassword("psw123");


        //authentification avec les bons identifiants
		mvc.perform(post("/api/authenticate")
			.contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
			.content(testUtil.asJsonString(userCred)))
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.token").exists());
           

		// Fausse tentative avec un username érroné
        userCred.setUsername("alice");
		mvc.perform(post("/api/authenticate")
			.contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
			.content(testUtil.asJsonString(userCred)))
			.andExpect(status().isUnauthorized());
	}

	@Test
	public void postRegisterTest() throws Exception {
		testUtil.cleanDB();

		UserRegisterDTO user = new UserRegisterDTO();
		user.setEmail("test@mail.fr");
		user.setUsername("tester");
		user.setPassword("psw12374");
		user.setRole(RoleEnum.ROLE_ARTIST.name());

		// création du compte
		mvc.perform(post("/api/register")
			.contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
			.content(testUtil.asJsonString(user)))
			.andExpect(status().isOk());
		
		// vérifier le création du nouveau compte
		Optional<UserEntity> optUser = userRepo.findByEmail("test@mail.fr");
		assertEquals(optUser.get().getUsername(), "tester");

		// mauvaise requête avec un @mail déjà existant
		user.setEmail("test@mail.fr");
		mvc.perform(post("/api/register")
			.contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
			.content(testUtil.asJsonString(user)))
			.andExpect(status().isConflict());
	}
}
