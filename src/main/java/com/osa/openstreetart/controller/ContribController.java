package com.osa.openstreetart.controller;

import java.util.ArrayList;
import java.util.List;

import com.osa.openstreetart.dto.OSAResponseDTO;
import com.osa.openstreetart.dto.PostContribDTO;
import com.osa.openstreetart.dto.PostNewContribDTO;
import com.osa.openstreetart.entity.ArtEntity;
import com.osa.openstreetart.entity.ContribEntity;
import com.osa.openstreetart.entity.RoleEnum;
import com.osa.openstreetart.exceptions.OSA400Exception;
import com.osa.openstreetart.exceptions.OSA401Exception;
import com.osa.openstreetart.exceptions.OSA404Exception;
import com.osa.openstreetart.service.ArtService;
import com.osa.openstreetart.service.ContribService;
import com.osa.openstreetart.service.JwtService;
import com.osa.openstreetart.service.UserService;
import com.osa.openstreetart.tranformator.ContribTransformator;
import com.osa.openstreetart.util.ApiRestController;
import com.osa.openstreetart.util.JwtUtil;
import com.osa.openstreetart.entity.UserEntity;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@ApiRestController
public class ContribController {

	@Autowired
	private JwtService jwtService;

	@Autowired
	private UserService userService;

	@Autowired
	private ContribService contribService;
	
	@Autowired
	private ArtService artService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private ContribTransformator contribTransf;

	private static final String TOKEN_PREFIX = "Bearer ";
	private static final String UNAUTHORIZE_MSG = "Unauthorized.";
	
	@PostMapping(value = "/contribs")
	public ResponseEntity<OSAResponseDTO> postContrib(@RequestHeader(value = "Authorization") String token,
			@RequestBody PostNewContribDTO dto) throws OSA400Exception {
		
		String username = jwtUtil.getUsernameFromToken(token.substring(TOKEN_PREFIX.length()));

		contribService.saveNewContrib(dto, userService.getOrFail(username));
		return ResponseEntity.ok(new OSAResponseDTO("Contribution created."));
	}

    @PostMapping(value = "/contribs/{art_id}")
	public ResponseEntity<OSAResponseDTO> postContribArtId(@RequestHeader(value = "Authorization") String token,
    		@PathVariable("art_id") Integer artId, @RequestBody PostContribDTO contrib)
			throws OSA400Exception {

		String username = jwtUtil.getUsernameFromToken(token.substring(TOKEN_PREFIX.length()));
		
		contribService.saveExistingContrib(contrib,
			userService.getOrFail(username),
			artId);
		return ResponseEntity.ok(new OSAResponseDTO("Contribution created."));
	}

    @DeleteMapping(value = "/contribs/{contrib_id}")
	public ResponseEntity<OSAResponseDTO> deleteContrib(@RequestHeader(value = "Authorization") String token,
			@PathVariable("contrib_id") Integer contribId) throws OSA401Exception, OSA404Exception, OSA400Exception {

		ContribEntity contrib = contribService.getOrFail(contribId);
		
		String username = jwtUtil.getUsernameFromToken(token.substring(TOKEN_PREFIX.length()));
		UserEntity user = userService.getOrFail(username);
		
		// Vérification si l'auteur de la requete est l'auteur de la contrib ou admin
		if (!contrib.getContributor().equals(user)
				&& !jwtService.getRolesByToken(token.substring(TOKEN_PREFIX.length())).contains(RoleEnum.ROLE_ADMIN))
			throw new OSA401Exception(UNAUTHORIZE_MSG);

		contribService.delete(contribId);
		return ResponseEntity.ok(new OSAResponseDTO("Contribution deleted"));
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping(value = "/contribs/{contrib_id}/accept")
	public ResponseEntity<OSAResponseDTO> acceptContrib(@RequestHeader(value = "Authorization") String token,
			@PathVariable("contrib_id") Integer contribId) throws OSA400Exception {
		
		ContribEntity contrib = contribService.getOrFail(contribId);

		contribService.acceptContrib(contrib);
    	return ResponseEntity.ok(new OSAResponseDTO("Contribution accepted"));
    }

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping(value = "/contribs/{contrib_id}/deny")
	public ResponseEntity<OSAResponseDTO> denyContrib(@RequestHeader(value = "Authorization") String token,
			@PathVariable("contrib_id") Integer contribId) throws OSA400Exception {

		ContribEntity contrib = contribService.getOrFail(contribId);
		
		contribService.denyContrib(contrib);
    	return ResponseEntity.ok(new OSAResponseDTO("Contribution refused"));
    }

	@GetMapping(value = "/contribs/arts/{art_id}")
    public ResponseEntity<OSAResponseDTO> getContribsOfArt(@PathVariable("art_id") Integer artId) throws OSA400Exception {
		ArtEntity art = artService.getOrFail(artId);
		
		List<ContribEntity> contribs = new ArrayList<>(contribService.findByArtId(art.getId()));

		return ResponseEntity.ok(
			new OSAResponseDTO(
				contribTransf.modelsToDtos(contribs)
			)
		);
	}

	@GetMapping(value = "/contribs/{contrib_id}")
	public ResponseEntity<OSAResponseDTO> getContribWithId(@PathVariable("contrib_id") Integer contribId) throws OSA400Exception {
		ContribEntity contrib = contribService.getOrFail(contribId);

		return ResponseEntity.ok(
			new OSAResponseDTO(contrib)
		);
	}

	@GetMapping(value = "/contribs/unapproved")
	public ResponseEntity<OSAResponseDTO> getUnapprovedContribs() {

		List<ContribEntity> contribs = new ArrayList<>();
		for (ContribEntity contrib : contribService.findAll()) {
			if (contrib.getApproved() == null)
				contribs.add(contrib);
		}

		return ResponseEntity.ok(
			new OSAResponseDTO(
				contribTransf.modelsToDtos(contribs)
			)
		);
	}

	@GetMapping(value = "/contribs/personnal")
	public ResponseEntity<OSAResponseDTO> getUserContribs(@RequestHeader(value = "Authorization") String token) throws OSA400Exception {
		String username = jwtUtil.getUsernameFromToken(token.substring(TOKEN_PREFIX.length()));
		UserEntity user = userService.getOrFail(username);
		
		List<ContribEntity> contribs = new ArrayList<>();
		//récupérer les contributions de l'utilisateur
		for (ContribEntity contrib : contribService.findAll()) {
			if (contrib.getContributor().equals(user))
				contribs.add(contrib);
		}
	
		return ResponseEntity.ok(new OSAResponseDTO(contribTransf.modelsToDtos(contribs)));
	}

	@GetMapping(value = "/contribs/users/{user_id}")
	public ResponseEntity<OSAResponseDTO> getUserContribs(
			@RequestHeader(value = "Authorization") String token,
			@PathVariable("user_id") Integer userId) throws OSA400Exception {

		UserEntity user = userService.getOrFail(userId);

		List<ContribEntity> contribs = new ArrayList<>();
		//récupérer les contributions de l'utilisateur
		for (ContribEntity contrib : contribService.findAll()) {
			if (contrib.getContributor().equals(user))
				contribs.add(contrib);
		}
	
		return ResponseEntity.ok(new OSAResponseDTO(contribTransf.modelsToDtos(contribs)));
	}
}
