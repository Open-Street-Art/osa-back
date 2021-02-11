package com.osa.openstreetart;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osa.openstreetart.entity.ArtEntity;
import com.osa.openstreetart.entity.RoleEnum;
import com.osa.openstreetart.entity.UserEntity;
import com.osa.openstreetart.service.JwtService;
import com.osa.openstreetart.util.JwtUtil;

import org.springframework.security.core.userdetails.UserDetails;

public class TestUtil {

	public static UserEntity createAdmin() {
		UserEntity admin = new UserEntity();
		admin.setEmail("admin@mail.fr");
		admin.setUsername("admin");
		admin.setPassword("psw123");

		Collection<RoleEnum> roles = new ArrayList<RoleEnum>();
		roles.add(RoleEnum.ROLE_ADMIN);
		admin.setRoles(roles);

		return admin;
	}

	public static ArtEntity createArt() {
		ArtEntity art = new ArtEntity();
		art.setName("Oeuvre");
		art.setCreationDateTime(LocalDateTime.now());
		art.setLongitude(1.5);
		art.setLatitude(3.2);
		return art;
	}

	public static String asJsonString(final Object obj) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }

	public static String getJWTwithUsername(String username, JwtService service, JwtUtil util) {
		UserDetails userDetails = service.loadUserByUsername(username);
		return util.generateToken(userDetails);
	}

}
