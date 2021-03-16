package com.osa.openstreetart;


import com.osa.openstreetart.dto.ContribDTO;
import com.osa.openstreetart.entity.ArtEntity;
import com.osa.openstreetart.entity.ContribEntity;
import com.osa.openstreetart.entity.UserEntity;
import com.osa.openstreetart.repository.ArtRepository;
import com.osa.openstreetart.repository.ContribRepository;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class ContribControllerTest {
    @Autowired
	UserRepository userRepo;

	@Autowired
	ArtRepository artRepo;

    @Autowired
	ContribRepository contribRepo;

    @Autowired
	JwtService jwtService;

	@Autowired
	JwtUtil jwtUtil;

    @Autowired
	MockMvc mvc;

	@Autowired
	TestUtil testUtil;

    @Test
    public void postContrib() throws Exception {
        testUtil.cleanDB();

        //utilisateur contributeur
        UserEntity contributor = testUtil.createUser();
        contributor = userRepo.save(contributor);

        String token = testUtil.getJWTwithUsername(contributor.getUsername());
        
        //l'oeuvre recevant la contribution
        ArtEntity art = testUtil.createArt();
		art.setAuthorName("Thomas");
		art = artRepo.save(art);
       
        //création du formulaire de contribution
        ContribDTO contribArt = new ContribDTO();
        contribArt.setName("belle Oeuvre");
        contribArt.setDescription("modification de le peinture");
        contribArt.setPicture1("shkbv.png");
		contribArt.setPicture2("dhvhbv.png");
		contribArt.setPicture3("hdhc.png");

        //enregister la contribution
		mvc.perform(post("/api/contrib/" + art.getId())
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(testUtil.asJsonString(contribArt)))
        .andExpect(status().isOk());

        //assertion
        Optional<ContribEntity> contrib = contribRepo.findByName("belle Oeuvre");
        assertTrue(contrib.isPresent());

    }

    @Test
    public void deleteContribTest() throws Exception {
        testUtil.cleanDB();

        //utilisateur contributeur
        UserEntity contributor = testUtil.createUser();
        userRepo.save(contributor);

        String token = testUtil.getJWTwithUsername(contributor.getUsername());

        //l'oeuvre recevant la contribution
        ArtEntity art = testUtil.createArt();
        art.setAuthorName("Thomas");
        art = artRepo.save(art);

        //la contribution à supprimer
        ContribEntity contrib = testUtil.createContrib();
        contrib.setLongitude(art.getLongitude());
        contrib.setLatitude(art.getLatitude());
        contrib.setArt(art);
        contrib = contribRepo.save(contrib);
         
         //supprimer la contribution
		mvc.perform(delete("/api/contrib/" + contrib.getId())
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

        //assertion
        Optional<ContribEntity> deletedContrib = contribRepo.findById(contrib.getId());
        assertFalse(deletedContrib.isPresent());
    }
}
