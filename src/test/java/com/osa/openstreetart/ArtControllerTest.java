package com.osa.openstreetart;

import com.osa.openstreetart.dto.ArtDTO;
import com.osa.openstreetart.entity.ArtEntity;
import com.osa.openstreetart.entity.UserEntity;
import com.osa.openstreetart.repository.ArtRepository;
import com.osa.openstreetart.repository.UserRepository;
import com.osa.openstreetart.service.JwtService;
import com.osa.openstreetart.util.JwtUtil;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class ArtControllerTest {
	
	@Autowired
	UserRepository userRepo;

	@Autowired
	ArtRepository artRepo;

	@Autowired
	JwtService jwtService;

	@Autowired
	JwtUtil jwtUtil;

	@Autowired
	MockMvc mvc;

	@Test
	public void patchArtTest() throws Exception {
		UserEntity author = TestUtil.createAdmin();
		userRepo.save(author);

		ArtEntity art = TestUtil.createArt();
		art.setAuthor(author);
		art = artRepo.save(art);

		String token = TestUtil.getJWTwithUsername(
			author.getUsername(), 
			jwtService, 
			jwtUtil
		);

		// Création du formulaire de modification
		ArtDTO artDTO = new ArtDTO();
		artDTO.setName("Nouvelle oeuvre");
		artDTO.setDescription("Une description");
		artDTO.setPicture1("1234");
		artDTO.setPicture2("");
		artDTO.setPicture3("");
		artDTO.setAuthor("Toto");

		// Changement des informations de l'art
		mvc.perform(patch("/api/art/" + art.getId())
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON)
			.content(TestUtil.asJsonString(artDTO)))
			.andExpect(status().isOk());

		// Fausse tentative
		mvc.perform(patch("/api/art/0")
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON)
			.content(TestUtil.asJsonString(artDTO)))
			.andExpect(status().isNotFound());

		// Verificatiom de la modification
		Optional<ArtEntity> optArt = artRepo.findByName("Nouvelle oeuvre");
		assertEquals(optArt.get().getAuthorName(), "Toto");
	}

}
