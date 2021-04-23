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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;

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
	CityRepository cityRepo;

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

		mvc.perform(post("/api/fav/arts/" + art.getId())
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		// Vérification que l'oeuvre est bien en favori
		MvcResult res = mvc.perform(get("/api/user/" + user.getId())
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk()).andReturn();

		assertTrue(res.getResponse().getContentAsString().contains("\"name\":\"Oeuvre\""));
	}

	@Test
	void deleteFavouriteArtTest() throws Exception {
		testUtil.cleanDB();

		UserEntity user = testUtil.createUser();
		user.setFavArts(new ArrayList<ArtEntity>());

		ArtEntity art = testUtil.createArt();
		art = artRepo.save(art);
		user.getFavArts().add(art);
		userRepo.save(user);

		String token = testUtil.getJWTwithUsername(user.getUsername());

		mvc.perform(delete("/api/fav/arts/" + art.getId())
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		// Vérification que l'oeuvre est bien en favori
		MvcResult res = mvc.perform(get("/api/user/" + user.getId())
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk()).andReturn();

		assertTrue(res.getResponse().getContentAsString().contains("\"favArts\":[]"));
	}

	@Test
	void postFavouriteArtistTest() throws Exception {
		testUtil.cleanDB();
		//utilisateur
		UserEntity user = testUtil.createUser();
		user = userRepo.save(user);

		// l'artist favori à ajouter
		UserEntity artist = testUtil.createArtist();
		artist = userRepo.save(artist);

		String token = testUtil.getJWTwithUsername(user.getUsername());

		mvc.perform(post("/api/fav/artists/" + artist.getId())
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		// vérifier l'ajout de l'artist favori
		user = userRepo.findById(user.getId()).get();
		assertSame(
			user.getFavArtists()
			.stream()
			.findFirst()
			.get().getId(), artist.getId());

	}

	@Test
	void deleteFavouriteArtistTest() throws Exception {
		testUtil.cleanDB();
		//utilisateur
		UserEntity user = testUtil.createUser();
		user = userRepo.save(user);
	
		// l'artist favori
		UserEntity artist = testUtil.createArtist();
		artist = userRepo.save(artist);

		user.setFavArtists(new ArrayList<UserEntity>());
		user.getFavArtists().add(artist);
		userRepo.save(user);
		
		String token = testUtil.getJWTwithUsername(user.getUsername());

		mvc.perform(delete("/api/fav/artists/" + artist.getId())
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		// vérifier le retrait de l'artist dans la liste
		user = userRepo.findById(user.getId()).get();
		assertTrue(user.getFavArtists().isEmpty());
	}

	@Test
	void postFavouriteCityTest() throws Exception {
		testUtil.cleanDB();

		UserEntity admin = testUtil.createAdmin();
		admin = userRepo.save(admin);

		String token = testUtil.getJWTwithUsername(admin.getUsername());

		//la ville favorite
		CityEntity cityFav = testUtil.createCity();
		cityFav = cityRepo.save(cityFav);

		//ajouter la ville à la liste favorite
		mvc.perform(post("/api/fav/cities/" + cityFav.getId())
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
		
		//la ville est ajoutée à la liste
		admin = userRepo.findById(admin.getId()).get();
		assertSame(
			admin.getFavCities()
			.stream()
			.findFirst()
			.get().getId(), cityFav.getId());
	}

	@Test
	void deleteFavouriteCityTest() throws Exception {
		testUtil.cleanDB();
		//utilisateur 
		UserEntity user = testUtil.createUser();
		user = userRepo.save(user);
	
		//la ville favorite
		CityEntity cityFav = testUtil.createCity();
		cityFav = cityRepo.save(cityFav);


		user.setFavCities(new ArrayList<CityEntity>());
		user.getFavCities().add(cityFav);
		userRepo.save(user);
		
		String token = testUtil.getJWTwithUsername(user.getUsername());

		mvc.perform(delete("/api/fav/cities/" + cityFav.getId())
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		// vérifier le retrait de la ville dans la liste
		user = userRepo.findById(user.getId()).get();
		assertTrue(user.getFavCities().isEmpty());
	}
}
