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
import org.springframework.web.bind.annotation.DeleteMapping;
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

	@DeleteMapping(value = "/fav/art/{art_id}")
	public ResponseEntity<OSAResponseDTO> deleteFavouriteArt(
			@RequestHeader(value = "Authorization") String token,
			@PathVariable("art_id") Integer artId) throws OSA400Exception {
		
		String username = jwtUtil.getUsernameFromToken(token.substring(tokenPrefix.length()));
		Optional<UserEntity> user = userRepo.findByUsername(username);
		if (!user.isPresent())
			throw new OSA400Exception(userNotFoundMsg);

		ArtEntity toDelete = null;
		for (ArtEntity art : user.get().getFavArts()) {
			if (art.getId().intValue() == artId.intValue()) {
				toDelete = art;
				break;
			}
		}
		if (toDelete != null)
			user.get().getFavArts().remove(toDelete);

		userRepo.save(user.get());
		
		return ResponseEntity.ok(new OSAResponseDTO("Art removed from the favourite arts list."));
	}

	@PostMapping(value = "/fav/artist/{artist_id}")
	public ResponseEntity<OSAResponseDTO> postFavouriteArtist(
			@RequestHeader(value = "Authorization") String token,
			@PathVariable("artist_id") Integer artistId) throws OSA400Exception, OSA404Exception {
		
		String username = jwtUtil.getUsernameFromToken(token.substring(tokenPrefix.length()));
		Optional<UserEntity> user = userRepo.findByUsername(username);
		if (!user.isPresent())
			throw new OSA400Exception(userNotFoundMsg);

		Optional<UserEntity> artist = userRepo.findById(artistId);
		if (!artist.isPresent())
			throw new OSA404Exception("artist not found");

		user.get().getFavArtists().add(artist.get());
		userRepo.save(user.get());
		
		return ResponseEntity.ok(new OSAResponseDTO("Artist added to the favourite artists list."));
	}

	@DeleteMapping(value = "/fav/artist/{artist_id}")
	public ResponseEntity<OSAResponseDTO> deleteFavouriteArtist(
			@RequestHeader(value = "Authorization") String token,
			@PathVariable("artist_id") Integer artistId) throws OSA400Exception, OSA404Exception {
		
		String username = jwtUtil.getUsernameFromToken(token.substring(tokenPrefix.length()));
		Optional<UserEntity> user = userRepo.findByUsername(username);
		if (!user.isPresent())
			throw new OSA400Exception(userNotFoundMsg);
		
		//l'artist est un artist favori de l'utilisateur
		Optional<UserEntity> artistFav = user.get().getFavArtists()
													.stream()
													.filter(u -> u.getId().equals(artistId))
													.findFirst();
		if(!artistFav.isPresent())
			throw new OSA404Exception("Artist not found in favorit list");
		
		//retirer l'artist de la liste
		user.get().getFavArtists().remove(artistFav.get());
		userRepo.save(user.get());
		
		return ResponseEntity.ok(new OSAResponseDTO("Artist remove to the favourite artists list."));
	}
}
