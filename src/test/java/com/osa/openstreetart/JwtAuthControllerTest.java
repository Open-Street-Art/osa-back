package com.osa.openstreetart;

import com.osa.openstreetart.entity.UserEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osa.openstreetart.dto.UserLoginDTO;
import com.osa.openstreetart.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class JwtAuthControllerTest {

    @Autowired
	private UserRepository userRepo;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private PasswordEncoder bcryptEncoder;

	@Autowired
	private TestUtil testUtil;
	@Test
	public void postAuthenticateTest() throws Exception {
        
		// Creation d'un utilisateur
		UserEntity user = testUtil.createUser();

		// Creation du l'objet de authentification : username, paswword
        UserLoginDTO userCred = new UserLoginDTO();
        userCred.setUsername(user.getUsername());
        userCred.setPassword(user.getPassword());

		//lors de l'authentification le password est comparé au password hashé dans la base
		user.setPassword(bcryptEncoder.encode(user.getPassword()));
		userRepo.save(user);

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

}
