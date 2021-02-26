package com.osa.openstreetart;

import com.osa.openstreetart.entity.ArtEntity;
import com.osa.openstreetart.repository.ArtRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
	private TestUtil testUtil;

	@Test
	void getSearchArtsTest() throws Exception {
		testUtil.cleanDB();
		ArtEntity art = testUtil.createArt();
		art = artRepo.save(art);

		MvcResult res = mvc.perform(get("/api/search/arts/oeuv")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk()).andReturn();

		assertEquals(true, res.getResponse().getContentAsString().contains("\"name\":\"Oeuvre\""));
	}
}
