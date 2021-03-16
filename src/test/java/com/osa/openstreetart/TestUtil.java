package com.osa.openstreetart;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osa.openstreetart.dto.ArtDTO;
import com.osa.openstreetart.entity.ArtEntity;
import com.osa.openstreetart.entity.CityEntity;
import com.osa.openstreetart.entity.ContribEntity;
import com.osa.openstreetart.entity.RoleEnum;
import com.osa.openstreetart.entity.UserEntity;
import com.osa.openstreetart.repository.ArtRepository;
import com.osa.openstreetart.repository.ContribRepository;
import com.osa.openstreetart.repository.UserRepository;
import com.osa.openstreetart.service.JwtService;
import com.osa.openstreetart.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class TestUtil {

	@Autowired
	ArtRepository artRepo;

	@Autowired
	UserRepository userRepo;

	@Autowired
	ContribRepository contribRepo;

	@Autowired
	JwtService jwtService;

	@Autowired
	JwtUtil jwtUtil;

	@Autowired
	private PasswordEncoder bcryptEncoder;

	public UserEntity createAdmin() {
		UserEntity admin = new UserEntity();
		admin.setEmail("admin@mail.fr");
		admin.setUsername("admin");
		admin.setPassword(bcryptEncoder.encode("psw123"));

		Collection<RoleEnum> roles = new ArrayList<RoleEnum>();
		roles.add(RoleEnum.ROLE_ADMIN);
		admin.setRoles(roles);

		return admin;
	}

	public UserEntity createUser() {
		UserEntity user = new UserEntity();
		user.setEmail("test@mail.fr");
		user.setUsername("tester");
		user.setIsPublic(false);
		user.setPassword(bcryptEncoder.encode("psw123"));

		Collection<RoleEnum> roles = new ArrayList<RoleEnum>();
		roles.add(RoleEnum.ROLE_USER);
		user.setRoles(roles);

		return user;
	}

	public UserEntity createArtist() {
		UserEntity user = new UserEntity();
		user.setEmail("test@mail.fr");
		user.setUsername("artist");
		user.setIsPublic(false);
		user.setPassword(bcryptEncoder.encode("psw123"));

		Collection<RoleEnum> roles = new ArrayList<RoleEnum>();
		roles.add(RoleEnum.ROLE_ARTIST);
		user.setRoles(roles);

		return user;
	}

	public ArtEntity createArt() {
		ArtEntity art = new ArtEntity();
		art.setName("Oeuvre");
		art.setCreationDateTime(LocalDateTime.now());
		art.setLongitude(1.5);
		art.setLatitude(3.2);
		return art;
	}

	public ArtDTO createArtDTO()
	{
		// Création du formulaire de modification
		ArtDTO artDTO = new ArtDTO();
		artDTO.setName("Nouvelle oeuvre");
		artDTO.setDescription("Une description");
		artDTO.setPicture1("art.png");
		artDTO.setPicture2("");
		artDTO.setPicture3("");
		artDTO.setLongitude(4.5);
		artDTO.setLatitude(3.2);
		return artDTO;
	}

	public ContribEntity createContrib() 
	{
		ContribEntity contrib = new ContribEntity();
		contrib.setName("Oeuvre 2");
        contrib.setDescription("description de l'oeuvre");
        contrib.setCreationDateTime(LocalDateTime.now());
		contrib.setApproved(false);
		return contrib;
	}

	public CityEntity createCity()
	{
		CityEntity city = new CityEntity();
		city.setName("rouen");
		return city;
	}
	
	public String asJsonString(final Object obj) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }

	public String getJWTwithUsername(String username) {
		UserDetails userDetails = jwtService.loadUserByUsername(username);
		return jwtUtil.generateToken(userDetails);
	}

	public void cleanDB() {
		contribRepo.deleteAll();
		artRepo.deleteAll();		
		userRepo.deleteAll();
	}

}
