package com.osa.openstreetart;

import com.osa.openstreetart.dto.ArtDTO;
import com.osa.openstreetart.dto.EditArtDTO;
import com.osa.openstreetart.entity.ArtEntity;
import com.osa.openstreetart.entity.CityEntity;
import com.osa.openstreetart.entity.UserEntity;
import com.osa.openstreetart.repository.ArtRepository;
import com.osa.openstreetart.repository.CityRepository;
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
import org.springframework.test.web.servlet.MvcResult;

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
	CityRepository cityRepo;

	@Autowired
	JwtService jwtService;

	@Autowired
	JwtUtil jwtUtil;

	@Autowired
	MockMvc mvc;

	@Autowired
	TestUtil testUtil;

	@Test
	void patchArtTest() throws Exception {
		testUtil.cleanDB();
		UserEntity author = testUtil.createAdmin();
		userRepo.save(author);

		ArtEntity art = testUtil.createArt();
		art.setAuthor(author);
		art = artRepo.save(art);

		String token = testUtil.getJWTwithUsername(author.getUsername());

		// Création du formulaire de modification
		EditArtDTO artDTO = new EditArtDTO();
		artDTO.setName("Nouvelle oeuvre");
		artDTO.setDescription("Une description");
		artDTO.setPicture1("1234");
		artDTO.setPicture2("");
		artDTO.setPicture3("");
		artDTO.setAuthor("Toto");

		// Changement des informations de l'art
		mvc.perform(patch("/api/admin/art/" + art.getId())
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON)
			.content(testUtil.asJsonString(artDTO)))
			.andExpect(status().isOk());

		// Fausse tentative
		mvc.perform(patch("/api/admin/art/0")
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON)
			.content(testUtil.asJsonString(artDTO)))
			.andExpect(status().isNotFound());

		// Verificatiom de la modification
		Optional<ArtEntity> optArt = artRepo.findByName("Nouvelle oeuvre");
		assertEquals(optArt.get().getAuthorName(), "Toto");
	}

	@Test
	void getArtsLocationsTest() throws Exception {
		ArtEntity art = testUtil.createArt();
		art = artRepo.save(art);
		MvcResult res = mvc.perform(get("/api/art/locations"))
			.andExpect(status().isOk()).andReturn();

		assertEquals(
			true,
			res.getResponse()
				.getContentAsString()
				.contains("\"latitude\":3.2,\"longitude\":1.5")
		);
	}

	@Test
	void postArtTest() throws Exception {
		testUtil.cleanDB();

		UserEntity author = testUtil.createAdmin();
		UserEntity createdAuthor = userRepo.save(author);
		String token = testUtil.getJWTwithUsername(author.getUsername());

		// création de ArtDTO
		ArtDTO art = testUtil.createArtDTO();

		//associé à un utilisateur spécifié
		art.setAuthor("Thomas");
		
		//la ville de l'oeuvre
		CityEntity city = testUtil.createCity();
		city = cityRepo.save(city);
		
		art.setCity_id(city.getId());
		
		// enregister l'oeuvre
		mvc.perform(post("/api/admin/art")
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
			.content(testUtil.asJsonString(art)))
			.andExpect(status().isOk());
		
		// vérification de l'ajout de la nouvlle oeuvre
		Optional<ArtEntity> optArt = artRepo.findByName("Nouvelle oeuvre");
		assertEquals(optArt.get().getAuthorName(), "Thomas");

		// mauvaise réquête avec  author_id de role ROLE_ADMIN
		art.setAuthor_id(createdAuthor.getId());
		mvc.perform(post("/api/admin/art")
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
			.content(testUtil.asJsonString(art)))
			.andExpect(status().isBadRequest());
	}

	@Test
	void deleteArtTest() throws Exception {
		testUtil.cleanDB();

		UserEntity admin = testUtil.createAdmin();
		userRepo.save(admin);

		String token = testUtil.getJWTwithUsername(admin.getUsername());

		// créer une nouvelle Oeuvre
		ArtEntity art = testUtil.createArt();
		art.setAuthor(admin);
		ArtEntity artToDelete = artRepo.save(art);

		// supprimer l'oeuvre
		mvc.perform(delete("/api/admin/art/" + artToDelete.getId())
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
		
		Optional<ArtEntity> optArt = artRepo.findById(artToDelete.getId());
		assertEquals(optArt.isPresent(),false);

		// Oeuvre inexistante
		mvc.perform(delete("/api/admin/art/9")
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	void getArtTest() throws Exception {
		testUtil.cleanDB();
		ArtEntity art = testUtil.createArt();
		artRepo.save(art);

		MvcResult res = mvc.perform(get("/api/search/arts/oeuv")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk()).andReturn();

		assertEquals(true, res.getResponse().getContentAsString().contains("\"name\":\"Oeuvre\""));
	}
}
