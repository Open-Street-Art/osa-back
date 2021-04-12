package com.osa.openstreetart;


import com.osa.openstreetart.dto.PostContribDTO;
import com.osa.openstreetart.dto.PostNewContribDTO;
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
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
class ContribControllerTest {
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
    void postContrib() throws Exception {
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
        PostContribDTO contribArt = new PostContribDTO();
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
    void postNewContrib() throws Exception {
        testUtil.cleanDB();

        //utilisateur contributeur
        UserEntity contributor = testUtil.createUser();
        contributor = userRepo.save(contributor);

        String token = testUtil.getJWTwithUsername(contributor.getUsername());
       
        //création du formulaire de contribution
        PostNewContribDTO contribArt = new PostNewContribDTO();

        contribArt.setName("Une oeuvre");
        contribArt.setDescription("Nouvelle oeuvre par un utilisateur");
        contribArt.setPicture1("FHFHFTH4646456DGDGDRGDRG");
		contribArt.setAuthorName("Toto");
		contribArt.setLatitude(56.78);
		contribArt.setLongitude(56.78);

        //enregister la contribution
		mvc.perform(post("/api/contrib")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(testUtil.asJsonString(contribArt)))
        .andExpect(status().isOk());

        //assertion
        Optional<ContribEntity> contrib = contribRepo.findByName("Une oeuvre");
        assertTrue(contrib.isPresent());

    }

    @Test
    void deleteContribTest() throws Exception {
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
		contrib.setContributor(contributor);
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

    @Test
    void acceptContribTest() throws Exception {
        testUtil.cleanDB();

         //l'administrateur
        UserEntity admin = testUtil.createAdmin();
        userRepo.save(admin);

        String token = testUtil.getJWTwithUsername(admin.getUsername());

        //l'oeuvre recevant la contribution
        ArtEntity art = testUtil.createArt();
        art.setAuthorName("Thomas");
        art = artRepo.save(art);

        //la contribution à accepter
        ContribEntity contrib = testUtil.createContrib(); 
        contrib.setLongitude(art.getLongitude());
        contrib.setLatitude(art.getLatitude());
        contrib.setArt(art);
        contrib = contribRepo.save(contrib);
        

        //accepter la contribution
		mvc.perform(post("/api/contrib/accept/" + contrib.getId())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        
        art = artRepo.findById(art.getId()).get();
        contrib = contribRepo.findById(contrib.getId()).get();

        //la contribution est acceptée 
        assertTrue(contrib.getApproved());
        assertEquals(art.getName(), contrib.getName());
    }

    @Test
    void denyContribTest() throws Exception {
        testUtil.cleanDB();

        //l'administrateur
        UserEntity admin = testUtil.createAdmin();
        userRepo.save(admin);

        String token = testUtil.getJWTwithUsername(admin.getUsername());

        //l'oeuvre recevant la contribution
        ArtEntity art = testUtil.createArt();
        art.setAuthorName("Thomas");
        art = artRepo.save(art);

        //la contribution à refuser
        ContribEntity contrib = testUtil.createContrib(); 
        contrib.setLongitude(art.getLongitude());
        contrib.setLatitude(art.getLatitude());
        contrib.setArt(art);
        contrib = contribRepo.save(contrib);
        

        //refuser la contribution
		mvc.perform(post("/api/contrib/deny/" + contrib.getId())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        
        art = artRepo.findById(art.getId()).get();
        contrib = contribRepo.findById(contrib.getId()).get();

        //la contribution est refusée 
        assertFalse(contrib.getApproved());
		assertNotSame(art.getName(), contrib.getName());
    }

    @Test
    void getContribsTest() throws Exception {
        testUtil.cleanDB();

        //l'oeuvre recevant la contribution
        ArtEntity art = testUtil.createArt();
        art.setAuthorName("toto");
        art = artRepo.save(art);

        //la contributions
        ContribEntity contrib = testUtil.createContrib(); 
        contrib.setLongitude(art.getLongitude());
        contrib.setLatitude(art.getLatitude());
        contrib.setArt(art);
        contrib = contribRepo.save(contrib);
 

        //retournner les contribution de l'oeuvre
        MvcResult res = mvc.perform(get("/api/contrib/" + art.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andReturn();
		
        assertTrue(res.getResponse().getContentAsString().contains("\"name\":\"Oeuvre 2\""));
    }

	@Test
	void getUnapprovedContribsTest() throws Exception {
		testUtil.cleanDB();

        ArtEntity art = testUtil.createArt();
        art.setAuthorName("toto");
        art = artRepo.save(art);

        ContribEntity contrib = testUtil.createContrib(); 
        contrib.setLongitude(art.getLongitude());
        contrib.setLatitude(art.getLatitude());
        contrib.setArt(art);
        contrib = contribRepo.save(contrib);
 

        MvcResult res = mvc.perform(get("/api/contrib/unapproved")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andReturn();
		

        assertTrue(res.getResponse().getContentAsString().contains("\"name\":\"Oeuvre 2\""));
	}
    @Test
	public void getUserContribsTest() throws Exception {
		testUtil.cleanDB();

        UserEntity user = testUtil.createUser();
        user = userRepo.save(user);

        String token = testUtil.getJWTwithUsername(user.getUsername());

        ArtEntity art = testUtil.createArt();
        art.setAuthorName("Thomas");
        art = artRepo.save(art);

        ContribEntity contrib = testUtil.createContrib(); 
        contrib.setLongitude(art.getLongitude());
        contrib.setLatitude(art.getLatitude());
        contrib.setArt(art);
        contrib.setContributor(user);
        contrib = contribRepo.save(contrib);
 

        MvcResult res = mvc.perform(get("/api/contrib/user/contribs")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andReturn();
		

        assertTrue(res.getResponse().getContentAsString().contains("\"name\":\"Oeuvre 2\""));
	}
}
