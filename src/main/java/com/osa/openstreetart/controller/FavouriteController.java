package com.osa.openstreetart.controller;

import java.util.Optional;

import com.osa.openstreetart.dto.OSAResponseDTO;
import com.osa.openstreetart.entity.ArtEntity;
import com.osa.openstreetart.entity.UserEntity;
import com.osa.openstreetart.exceptions.OSA400Exception;
import com.osa.openstreetart.exceptions.OSA404Exception;
import com.osa.openstreetart.repository.ArtRepository;
import com.osa.openstreetart.repository.UserRepository;
import com.osa.openstreetart.util.ApiRestController;
import com.osa.openstreetart.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@ApiRestController
public class FavouriteController {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ArtRepository artRepo;

	private static String tokenPrefix = "Bearer ";
	private static String userNotFoundMsg = "User not found.";
	private static String artNotFoundMsg = "Art not found.";
	
	@PostMapping(value = "/fav/art/{art_id}")
	public ResponseEntity<OSAResponseDTO> postFavouriteArt(
			@RequestHeader(value = "Authorization") String token,
			@PathVariable("art_id") Integer artId) throws OSA400Exception, OSA404Exception {
		
		String username = jwtUtil.getUsernameFromToken(token.substring(tokenPrefix.length()));
		Optional<UserEntity> user = userRepo.findByUsername(username);
		if (!user.isPresent())
			throw new OSA400Exception(userNotFoundMsg);

		Optional<ArtEntity> art = artRepo.findById(artId);
		if (!art.isPresent())
			throw new OSA404Exception(artNotFoundMsg);
		
		user.get().getFavArts().add(art.get());
		userRepo.save(user.get());
		
		return ResponseEntity.ok(new OSAResponseDTO("Art added to the favourite arts list."));
	}

}
