package com.osa.openstreetart;

import com.osa.openstreetart.entity.ArtEntity;
import com.osa.openstreetart.entity.UserEntity;
import com.osa.openstreetart.repository.ArtRepository;
import com.osa.openstreetart.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FavouriteControllerTest {
	
	@Autowired
	TestUtil testUtil;

	@Autowired
	UserRepository userRepo;

	@Autowired
	ArtRepository artRepo;

	@Autowired
	MockMvc mvc;

	@Test
	void postFavouriteArtTest() throws Exception {
		testUtil.cleanDB();

		UserEntity user = testUtil.createUser();
		userRepo.save(user);

		ArtEntity art = testUtil.createArt();
		art = artRepo.save(art);

		String token = testUtil.getJWTwithUsername(user.getUsername());

		mvc.perform(post("/api/fav/art/" + art.getId())
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		// Vérification que l'oeuvre est bien en favori
		MvcResult res = mvc.perform(get("/api/user/" + user.getId())
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk()).andReturn();

		assertTrue(res.getResponse().getContentAsString().contains("\"favArts\":[" + art.getId() +"]"));
	}

}
