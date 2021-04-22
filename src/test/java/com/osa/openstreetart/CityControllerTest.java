package com.osa.openstreetart;

import com.osa.openstreetart.entity.ArtEntity;
import com.osa.openstreetart.entity.CityEntity;
import com.osa.openstreetart.repository.ArtRepository;
import com.osa.openstreetart.repository.CityRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CityControllerTest {
	
	@Autowired
	TestUtil testUtil;
	
	@Autowired
	CityRepository cityRepo;

	@Autowired
	ArtRepository artRepo;

	@Autowired
	MockMvc mvc;

	@Test
	void getCityTest() throws Exception {
		testUtil.cleanDB();

		CityEntity city = testUtil.createCity();
		cityRepo.save(city);

		MvcResult res = mvc.perform(get("/api/cities/" + city.getId())
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk()).andReturn();

		assertTrue(
			res.getResponse()
				.getContentAsString()
				.contains("rouen")
		);
	}

	@Test
	void getArtsByCityTest() throws Exception {
		testUtil.cleanDB();

		CityEntity city = testUtil.createCity();
		cityRepo.save(city);

		ArtEntity art = testUtil.createArt();
		art.setCity(city);
		artRepo.save(art);

		MvcResult res = mvc.perform(get("/api/cities/" + city.getId() + "/arts")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk()).andReturn();

		assertTrue(res.getResponse().getContentAsString().contains("Oeuvre"));
	}

}
