package com.osa.openstreetart;

import com.osa.openstreetart.entity.ArtEntity;
import com.osa.openstreetart.entity.CityEntity;
import com.osa.openstreetart.entity.UserEntity;
import com.osa.openstreetart.repository.ArtRepository;
import com.osa.openstreetart.repository.CityRepository;
import com.osa.openstreetart.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SearchControllerTest {
	
	@Autowired
	private MockMvc mvc;

	@Autowired
	private ArtRepository artRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private CityRepository cityRepo;

	@Autowired
	private TestUtil testUtil;

	@Test
	void getSearchUsersTest() throws Exception {
		testUtil.cleanDB();
		UserEntity user = testUtil.createUser();
		user.setUsername("toto");
		userRepo.save(user);
		MvcResult res = mvc.perform(get(("/api/search/users/") + user.getUsername())
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk()).andReturn();
	}

	@Test
	void getSearchArtsTest() throws Exception {
		testUtil.cleanDB();
		ArtEntity art = testUtil.createArt();
		artRepo.save(art);

		MvcResult res = mvc.perform(get("/api/search/arts/oeuv")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk()).andReturn();

		assertTrue(res.getResponse().getContentAsString().contains("\"name\""));
	}

	@Test
	void getSearchArtsByArtistNameTest() throws Exception {
		// Test de la recherche par le champ string nom d'artiste d'une oeuvre
		testUtil.cleanDB();
		ArtEntity art = testUtil.createArt();
		art.setAuthorName("tata");
		artRepo.save(art);

		MvcResult res = mvc.perform(get("/api/search/arts/artist/tat")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk()).andReturn();

		assertTrue(res.getResponse().getContentAsString().contains("\"name\":\"Oeuvre\""));

		// TEst de la recherche par la référence de l'entité avec ce nom
		testUtil.cleanDB();
		UserEntity artist = testUtil.createArtist();
		userRepo.save(artist);

		art = testUtil.createArt();
		art.setAuthor(artist);
		artRepo.save(art);

		res = mvc.perform(get("/api/search/arts/artist/arti")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk()).andReturn();

		assertTrue(res.getResponse().getContentAsString().contains("\"name\":\"Oeuvre\""));
	}

	@Test
	void getSearchCitiesTest() throws Exception {
		testUtil.cleanDB();

		CityEntity city = new CityEntity();
		city.setName("rouen");
		city = cityRepo.save(city);

		ArtEntity art = testUtil.createArt();
		art.setAuthorName("bob");
		art.setCity(city);
		artRepo.save(art);


		ArtEntity art1 = testUtil.createArt();
		art.setAuthorName("alice");
		art.setCity(city);
		artRepo.save(art1);

	   mvc.perform(get("/api/search/cities/rouen")
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.data").exists())
		.andExpect(MockMvcResultMatchers.jsonPath("$.data[*].id").isNotEmpty());

	}
}
