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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class JwtAuthControllerTest {
    @Autowired
	UserRepository userRepo;

	@Autowired
	MockMvc mvc;

	@Test
	public void postAuthenticateTest() throws Exception {
        
		// Creation d'un utilisateur
		UserEntity user = new UserEntity();
		user.setEmail("bob@gmail.com");
		user.setUsername("bob789");
		user.setPassword("bobzqw421");
		userRepo.save(user);

		// Creation du l'objet de authentification : username, paswword
        UserLoginDTO userCred = new UserLoginDTO();
        userCred.setUsername(user.getUsername());
        userCred.setPassword(user.getPassword());

        //authentification
		mvc.perform(patch("/api/authenticate")
			.contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
			.content(asJsonString(userCred)))
			.andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.jwttoken").exists());

		// Fausse tentative avec un username érroné
        userCred.setUsername("alice");

		mvc.perform(patch("/api/authenticate")
			.contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
			.content(asJsonString(userCred)))
			.andExpect(status().isBadRequest());

	}

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
