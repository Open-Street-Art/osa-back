package com.osa.openstreetart.controller;

import java.util.ArrayList;
import java.util.Collection;

import com.osa.openstreetart.dto.OSAResponseDTO;
import com.osa.openstreetart.dto.UserProfileDTO;
import com.osa.openstreetart.entity.ArtEntity;
import com.osa.openstreetart.entity.UserEntity;
import com.osa.openstreetart.exceptions.OSA400Exception;
import com.osa.openstreetart.service.ArtService;
import com.osa.openstreetart.service.UserService;
import com.osa.openstreetart.util.ApiRestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@ApiRestController
public class SearchController {
	
	@Autowired
	private ArtService artService;

	@Autowired
	private UserService userService;

	private static final String EMPTY_SEARCH_MSG = "Empty search content.";

	@GetMapping(value = "/search/users/{content}")
	public ResponseEntity<OSAResponseDTO> getSearchUsers(@PathVariable("content") String content)
			throws OSA400Exception {
		if (content.isEmpty() || content.isBlank())
			throw new OSA400Exception(EMPTY_SEARCH_MSG);

		Collection<UserProfileDTO> searchResult = new ArrayList<>();
		
		for (UserEntity user : userService.findByUsernameWithSub(content)) {
			searchResult.add(userService.loadUserProfileDTO(user));
		}

		return ResponseEntity.ok(
				new OSAResponseDTO(searchResult)
		);
	}

	@GetMapping(value = "/search/arts/{content}")
	public ResponseEntity<OSAResponseDTO> getSearchArts(@PathVariable("content") String content)
			throws OSA400Exception {
		if (content.isEmpty() || content.isBlank())
			throw new OSA400Exception(EMPTY_SEARCH_MSG);

		return ResponseEntity.ok(
			new OSAResponseDTO(artService.findByNameWithSub(content))
		);
	}

	@GetMapping(value = "/search/arts/artists/{username}")
	public ResponseEntity<OSAResponseDTO> getSearchArtsByArtist(@PathVariable("username") String username)
			throws OSA400Exception {
		if (username.isEmpty() || username.isBlank())
			throw new OSA400Exception(EMPTY_SEARCH_MSG);

		// Résultat à renvoyer
		Collection<ArtEntity> result = new ArrayList<>(
			artService.findByAuthorNameWithSub(username));

		// Récuperation de la liste d'artistes par référence
		Collection<UserEntity> artists = userService.findByUsernameWithSub(username);

		for (UserEntity artist : artists)
			result.addAll(artService.findByAuthorId(artist.getId()));

		return ResponseEntity.ok(new OSAResponseDTO(result));
	}

	@GetMapping(value = "/search/cities/{content}")
	public ResponseEntity<OSAResponseDTO> getSearchCities(@PathVariable("content") String content)
			throws OSA400Exception {
		if (content.isEmpty() || content.isBlank())
			throw new OSA400Exception(EMPTY_SEARCH_MSG);

		return ResponseEntity.ok(new OSAResponseDTO(artService.findByCitiesName(content)));
	}
}
