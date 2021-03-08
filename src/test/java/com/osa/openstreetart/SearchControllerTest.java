package com.osa.openstreetart;

import com.osa.openstreetart.entity.ArtEntity;
import com.osa.openstreetart.entity.UserEntity;
import com.osa.openstreetart.repository.ArtRepository;
import com.osa.openstreetart.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SearchControllerTest {
	
	@Autowired
	private MockMvc mvc;

	@Autowired
	private ArtRepository artRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private TestUtil testUtil;

	@Test
	void getSearchArtsTest() throws Exception {
		testUtil.cleanDB();
		ArtEntity art = testUtil.createArt();
		artRepo.save(art);

		MvcResult res = mvc.perform(get("/api/search/arts/oeuv")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk()).andReturn();

		assertTrue(res.getResponse().getContentAsString().contains("\"name\":\"Oeuvre\""));
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
}
