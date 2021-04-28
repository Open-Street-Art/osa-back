package com.osa.openstreetart.controller;

import java.util.Collection;
import java.util.Optional;

import com.osa.openstreetart.dto.OSAResponseDTO;
import com.osa.openstreetart.entity.ArtEntity;
import com.osa.openstreetart.entity.CityEntity;
import com.osa.openstreetart.entity.RoleEnum;
import com.osa.openstreetart.entity.UserEntity;
import com.osa.openstreetart.exceptions.OSA400Exception;
import com.osa.openstreetart.exceptions.OSA401Exception;
import com.osa.openstreetart.exceptions.OSA404Exception;
import com.osa.openstreetart.exceptions.OSA500Exception;
import com.osa.openstreetart.service.ArtService;
import com.osa.openstreetart.service.CityService;
import com.osa.openstreetart.service.JwtService;
import com.osa.openstreetart.service.UserService;
import com.osa.openstreetart.util.ApiRestController;
import com.osa.openstreetart.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@ApiRestController
@Slf4j
public class FavouriteController {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private UserService userService;

	@Autowired
	private ArtService artService;

	@Autowired
	private CityService cityService;

	private static final String TOKEN_PREFIX = "Bearer ";
	private static final String UNAUTHORIZE_MSG = "Unauthorized.";
	
	@ApiOperation(value = "Ajouter une œuvre dans la liste des œuvres Favorites.")
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "œuvre ajouté à la liste favorite ."),
		@ApiResponse(code = 400, message = "Utilisateur non identifié."),
		@ApiResponse(code = 401, message = "Token invalid ."),
		@ApiResponse(code = 404, message = "œuvre non trouvée ."),
		@ApiResponse(code = 500, message = "œuvre déjà trouvée dans la liste favorite.")
	})
	@PostMapping(value = "/fav/arts/{art_id}")
	public ResponseEntity<OSAResponseDTO> postFavouriteArt(
			@RequestHeader(value = "Authorization") String token,
			@PathVariable("art_id") Integer artId) throws OSA400Exception, OSA401Exception, OSA404Exception, OSA500Exception {
		
		Collection<RoleEnum> userRoles = jwtService.getRolesByToken(token.substring(TOKEN_PREFIX.length()));
		if (!userRoles.contains(RoleEnum.ROLE_USER) && !userRoles.contains(RoleEnum.ROLE_ARTIST)
			&&	!jwtService.getRolesByToken(token.substring(TOKEN_PREFIX.length())).contains(RoleEnum.ROLE_ADMIN))
				throw new OSA401Exception(UNAUTHORIZE_MSG);

		String username = jwtUtil.getUsernameFromToken(token.substring(TOKEN_PREFIX.length()));
		UserEntity user = userService.getOrFail(username);
		
		ArtEntity art = artService.getOrFail(artId);
		
		boolean isFavArt = false;
		for(ArtEntity FavArt: user.getFavArts())
		{
			if(FavArt.getId().intValue() == artId.intValue())
			{
				isFavArt = true;
				break;
			}
		}
		if (isFavArt) 
			throw new OSA500Exception("art is already in favorite list");

		user.getFavArts().add(art);
		userService.save(user);
		log.info("Art : " + artId + " added to User :" + user.getId() + "favourite list");

		return ResponseEntity.ok(new OSAResponseDTO("Art added to the favourite arts list."));
	}

	@ApiOperation(value = "Retirer une œuvre dans la liste des œuvres Favorites.")
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "œuvre retirée de la liste favorite ."),
		@ApiResponse(code = 400, message = "Utilisateur non identifié."),
		@ApiResponse(code = 401, message = "Token invalid ."),
		@ApiResponse(code = 404, message = "œuvre non trouvée dans la liste favorite.")
	})
	@DeleteMapping(value = "/fav/arts/{art_id}")
	public ResponseEntity<OSAResponseDTO> deleteFavouriteArt(
			@RequestHeader(value = "Authorization") String token,
			@PathVariable("art_id") Integer artId) throws OSA400Exception, OSA401Exception,OSA404Exception {
		
		Collection<RoleEnum> userRoles = jwtService.getRolesByToken(token.substring(TOKEN_PREFIX.length()));
		if (!userRoles.contains(RoleEnum.ROLE_USER) && !userRoles.contains(RoleEnum.ROLE_ARTIST)
			&&	!jwtService.getRolesByToken(token.substring(TOKEN_PREFIX.length())).contains(RoleEnum.ROLE_ADMIN))
				throw new OSA401Exception(UNAUTHORIZE_MSG);
	
		String username = jwtUtil.getUsernameFromToken(token.substring(TOKEN_PREFIX.length()));
		UserEntity user = userService.getOrFail(username);
		
		ArtEntity toDelete = null;
		for (ArtEntity art : user.getFavArts()) {
			if (art.getId().intValue() == artId.intValue()) {
				toDelete = art;
				break;
			}
		}

		if	(toDelete == null)
			throw new OSA404Exception("Art not found in favorit list");
		
		user.getFavArts().remove(toDelete);
		userService.save(user);
		log.info("Art : " + artId + " removed to User :" + user.getId() + "favourite list");

		return ResponseEntity.ok(new OSAResponseDTO("Art removed from the favourite arts list."));
	}

	@ApiOperation(value = "Ajouter un artiste dans la liste des artistes Favoris.")
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Artiste ajouté à la liste favorite ."),
		@ApiResponse(code = 400, message = "Utilisateur non identifié."),
		@ApiResponse(code = 401, message = "Token invalid ."),
		@ApiResponse(code = 404, message = "Artiste non trouvé ."),
		@ApiResponse(code = 500, message = "Artiste déjà trouvé dans la liste favorite.")
	})
	@PostMapping(value = "/fav/artists/{artist_id}")
	public ResponseEntity<OSAResponseDTO> postFavouriteArtist(
			@RequestHeader(value = "Authorization") String token,
			@PathVariable("artist_id") Integer artistId) throws OSA400Exception, OSA401Exception,OSA404Exception, OSA500Exception {
		
		Collection<RoleEnum> userRoles = jwtService.getRolesByToken(token.substring(TOKEN_PREFIX.length()));
		if (!userRoles.contains(RoleEnum.ROLE_USER) && !userRoles.contains(RoleEnum.ROLE_ARTIST)
			    &&	!jwtService.getRolesByToken(token.substring(TOKEN_PREFIX.length())).contains(RoleEnum.ROLE_ADMIN))
			throw new OSA401Exception(UNAUTHORIZE_MSG);
	
		String username = jwtUtil.getUsernameFromToken(token.substring(TOKEN_PREFIX.length()));
		UserEntity user = userService.getOrFail(username);
		
		if (user.getId().equals(artistId))
				throw new OSA400Exception("Invalid artist Id");

		//l'artist est present dans la liste favorite
		Optional<UserEntity> artistFav = user.getFavArtists()
													.stream()
													.filter(u -> u.getId().equals(artistId))
													.findFirst();
		if (artistFav.isPresent())
			throw new OSA500Exception("Artist is already in favourite artists list");
		
		UserEntity artist = userService.getOrFail(artistId);
		
		user.getFavArtists().add(artist);
		userService.save(user);
		log.info("Artist : " + artistId + " deleted to User :" + user.getId() + "favourite list");

		return ResponseEntity.ok(new OSAResponseDTO("Artist added to the favourite artists list."));
	}

	@ApiOperation(value = "Retirer un artist dans la liste des artistes Favoris.")
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Artiste retiré de la liste favorite ."),
		@ApiResponse(code = 400, message = "Utilisateur non identifié."),
		@ApiResponse(code = 401, message = "Token invalid ."),
		@ApiResponse(code = 404, message = "Artiste non trouvé dans la liste favorite.")
	})
	@DeleteMapping(value = "/fav/artists/{artist_id}")
	public ResponseEntity<OSAResponseDTO> deleteFavouriteArtist(
			@RequestHeader(value = "Authorization") String token,
			@PathVariable("artist_id") Integer artistId) throws OSA400Exception,OSA401Exception,OSA404Exception {
		
		Collection<RoleEnum> userRoles = jwtService.getRolesByToken(token.substring(TOKEN_PREFIX.length()));
		if (!userRoles.contains(RoleEnum.ROLE_USER) && !userRoles.contains(RoleEnum.ROLE_ARTIST)
				&&	!jwtService.getRolesByToken(token.substring(TOKEN_PREFIX.length())).contains(RoleEnum.ROLE_ADMIN))
			throw new OSA401Exception(UNAUTHORIZE_MSG);
		
		String username = jwtUtil.getUsernameFromToken(token.substring(TOKEN_PREFIX.length()));
		UserEntity user = userService.getOrFail(username);
		
		
		//l'artist est un artist favori de l'utilisateur
		Optional<UserEntity> artistFav = user.getFavArtists()
													.stream()
													.filter(u -> u.getId().equals(artistId))
													.findFirst();
		if(artistFav.isEmpty())
			throw new OSA404Exception("Artist not found in favorit list");
		
		//retirer l'artist de la liste
		user.getFavArtists().remove(artistFav.get());
		userService.save(user);
		log.info("Artist : " + artistId + " removed to User :" + user.getId() + "favourite list");

		return ResponseEntity.ok(new OSAResponseDTO("Artist remove to the favourite artists list."));
	}

	@ApiOperation(value = "Ajouter une ville dans la liste des villes Favorites.")
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Ville ajoutée à la liste favorite ."),
		@ApiResponse(code = 400, message = "Utilisateur non identifié ."),
		@ApiResponse(code = 401, message = "Token invalid ."),
		@ApiResponse(code = 404, message = "Ville non trouvée ."),
		@ApiResponse(code = 500, message = "Ville déjà trouvée dans la liste favorite .")
	})
	@PostMapping(value = "/fav/cities/{city_id}")
	public ResponseEntity<OSAResponseDTO> postFavouriteCity(
			@RequestHeader(value = "Authorization") String token,
			@PathVariable("city_id") Integer cityId) throws OSA400Exception, OSA401Exception, OSA404Exception, OSA500Exception {
		
		Collection<RoleEnum> userRoles = jwtService.getRolesByToken(token.substring(TOKEN_PREFIX.length()));
		if (!userRoles.contains(RoleEnum.ROLE_USER) && !userRoles.contains(RoleEnum.ROLE_ARTIST)
				&&	!jwtService.getRolesByToken(token.substring(TOKEN_PREFIX.length())).contains(RoleEnum.ROLE_ADMIN))
			throw new OSA401Exception(UNAUTHORIZE_MSG);
		
		String username = jwtUtil.getUsernameFromToken(token.substring(TOKEN_PREFIX.length()));
		UserEntity user = userService.getOrFail(username);
		
		//la ville est présente dans la liste favorite
		Optional<CityEntity> cityFav = user.getFavCities()
													.stream()
													.filter(c -> c.getId().equals(cityId))
													.findFirst();
		if(cityFav.isPresent())
			throw new OSA500Exception("City is already in favourite Cities list");
		
		CityEntity cityFavToAdd = cityService.getOrFail(cityId);

		user.getFavCities().add(cityFavToAdd);
		userService.save(user);
		log.info("City : " + cityId + " added to User :" + user.getId() + " favourite list");

		return ResponseEntity.ok(new OSAResponseDTO("City added to the favourite Cities list."));
	}

	@ApiOperation(value = "Retirer une ville dans la liste des villes Favorites.")
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Ville retirée de la la liste favorite ."),
		@ApiResponse(code = 400, message = "Utilisateur non identifié ."),
		@ApiResponse(code = 401, message = "Token invalid ."),
		@ApiResponse(code = 404, message = "Ville non trouvée dans la liste favorite.")
	})
	@DeleteMapping(value = "/fav/cities/{city_id}")
	public ResponseEntity<OSAResponseDTO> deleteFavouriteCity(
			@RequestHeader(value = "Authorization") String token,
			@PathVariable("city_id") Integer cityId) throws OSA400Exception,OSA401Exception, OSA404Exception {
		
		Collection<RoleEnum> userRoles = jwtService.getRolesByToken(token.substring(TOKEN_PREFIX.length()));
		if (!userRoles.contains(RoleEnum.ROLE_USER) && !userRoles.contains(RoleEnum.ROLE_ARTIST)
				&&	!jwtService.getRolesByToken(token.substring(TOKEN_PREFIX.length())).contains(RoleEnum.ROLE_ADMIN))
			throw new OSA401Exception(UNAUTHORIZE_MSG);
		
		String username = jwtUtil.getUsernameFromToken(token.substring(TOKEN_PREFIX.length()));
		UserEntity user = userService.getOrFail(username);
		
		//la ville est une ville favorite
		CityEntity cityFav = null;
		for(CityEntity city: user.getFavCities())
		{
			if(city.getId().equals(cityId))
			{
				cityFav = city;
				break;
			}
		}

		if(cityFav == null)
			throw new OSA404Exception("City not found in favorit list");
		
		user.getFavCities().remove(cityFav);
		userService.save(user);
		log.info("City : " + cityId + " removed to User :" + user.getId() + " favourite list");

		return ResponseEntity.ok(new OSAResponseDTO("City remove to the favourite Cities list."));
	}
}

