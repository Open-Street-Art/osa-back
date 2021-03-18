package com.osa.openstreetart.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.osa.openstreetart.dto.OSAResponseDTO;
import com.osa.openstreetart.dto.PostContribDTO;
import com.osa.openstreetart.dto.PostNewContribDTO;
import com.osa.openstreetart.entity.ArtEntity;
import com.osa.openstreetart.entity.ContribEntity;
import com.osa.openstreetart.entity.RoleEnum;
import com.osa.openstreetart.exceptions.OSA400Exception;
import com.osa.openstreetart.exceptions.OSA401Exception;
import com.osa.openstreetart.exceptions.OSA404Exception;
import com.osa.openstreetart.repository.ArtRepository;
import com.osa.openstreetart.repository.ContribRepository;
import com.osa.openstreetart.repository.UserRepository;
import com.osa.openstreetart.service.ContribService;
import com.osa.openstreetart.service.JwtService;
import com.osa.openstreetart.tranformator.ContribTransformator;
import com.osa.openstreetart.util.ApiRestController;
import com.osa.openstreetart.util.JwtUtil;
import com.osa.openstreetart.entity.UserEntity;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ApiRestController
public class ContribController {

	@Autowired
	private JwtService jwtService;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ContribService contribService;

	@Autowired
	private ContribRepository contribRepo;

	@Autowired
	private ArtRepository artRepo;
	
	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private ContribTransformator contribTransf;

	private static String tokenPrefix = "Bearer ";
	private static String unauthorizedMsg = "Unauthorized.";
	private static String userNotFoundMsg = "User not found.";
	private static String contribNotFoundMsg = "Contribution not found.";
	
	@PostMapping(value = "/contrib")
	public ResponseEntity<OSAResponseDTO> postContrib(@RequestHeader(value = "Authorization") String token,
			@RequestBody PostNewContribDTO dto) throws OSA400Exception {
		
		String username = jwtUtil.getUsernameFromToken(token.substring(tokenPrefix.length()));
		Optional<UserEntity> contribUser = userRepo.findByUsername(username);
		if (!contribUser.isPresent())
			throw new OSA400Exception(userNotFoundMsg);

		ContribEntity contrib = new ContribEntity();
		contrib.setName(dto.getName());
		contrib.setDescription(dto.getDescription());
		Collection<String> pictures = new ArrayList<>();
		pictures.add(dto.getPicture1());
		pictures.add(dto.getPicture2());
		pictures.add(dto.getPicture3());
		contrib.setPictures(pictures);
		contrib.setLongitude(dto.getLongitude());
		contrib.setLatitude(dto.getLatitude());
		contrib.setAuthorName(dto.getAuthorName());
		contrib.setCreationDateTime(LocalDateTime.now());
		contrib.setContributor(contribUser.get());

		contribRepo.save(contrib);
		
		return ResponseEntity.ok(new OSAResponseDTO("Contribution created."));
	}

    @PostMapping(value = "/contrib/{art_id}")
	public ResponseEntity<OSAResponseDTO> postContribArtId(@RequestHeader(value = "Authorization") String token,
    		@PathVariable("art_id") Integer artId, @RequestBody PostContribDTO contrib)
			throws OSA400Exception {

		String username = jwtUtil.getUsernameFromToken(token.substring(tokenPrefix.length()));
		Optional<UserEntity> contribUser = userRepo.findByUsername(username);
		if (!contribUser.isPresent())
			throw new OSA400Exception(userNotFoundMsg);
		
		contribService.save(contrib, contribUser.get(), artId);
		return ResponseEntity.ok(new OSAResponseDTO("Contribution created."));
	}

    @DeleteMapping(value = "/contrib/{contrib_id}")
	public ResponseEntity<OSAResponseDTO> deleteContrib(@RequestHeader(value = "Authorization") String token,
			@PathVariable("contrib_id") Integer contribId) throws OSA401Exception, OSA404Exception, OSA400Exception {

		Optional<ContribEntity> contrib = contribRepo.findById(contribId);
		if (!contrib.isPresent())
			throw new OSA400Exception(contribNotFoundMsg);

		String username = jwtUtil.getUsernameFromToken(token.substring(tokenPrefix.length()));
		Optional<UserEntity> optionalUser = userRepo.findByUsername(username);
		if (!optionalUser.isPresent())
			throw new OSA400Exception(userNotFoundMsg);

		// Vérification si l'auteur de la requete est l'auteur de la contrib ou admin
		if (!contrib.get().getContributor().equals(optionalUser.get())
				&& !jwtService.getRolesByToken(token.substring(tokenPrefix.length())).contains(RoleEnum.ROLE_ADMIN))
			throw new OSA401Exception(unauthorizedMsg);										

		contribService.delete(contribId);
		return ResponseEntity.ok(new OSAResponseDTO("Contribution deleted"));
	}

	@PostMapping(value = "/contrib/accept/{contrib_id}")
	public ResponseEntity<OSAResponseDTO> acceptContrib(@RequestHeader(value = "Authorization") String token,
			@PathVariable("contrib_id") Integer contribId) throws OSA404Exception, OSA401Exception, OSA400Exception {

		if (!jwtService.getRolesByToken(token.substring(tokenPrefix.length())).contains(RoleEnum.ROLE_ADMIN))
			throw new OSA401Exception(unauthorizedMsg);
		
		Optional<ContribEntity> contrib = contribRepo.findById(contribId);
		if(!contrib.isPresent())
			throw new OSA404Exception(contribNotFoundMsg);
		
		contribService.acceptContrib(contrib.get());
    	return ResponseEntity.ok(new OSAResponseDTO("Contribution accepted"));
    }

	@PostMapping(value = "/contrib/deny/{contrib_id}")
	public ResponseEntity<OSAResponseDTO> denyContrib(@RequestHeader(value = "Authorization") String token,
			@PathVariable("contrib_id") Integer contribId) throws OSA401Exception, OSA404Exception, OSA400Exception {

		if (!jwtService.getRolesByToken(token.substring(tokenPrefix.length())).contains(RoleEnum.ROLE_ADMIN))
			throw new OSA401Exception(unauthorizedMsg);

		Optional<ContribEntity> contrib = contribRepo.findById(contribId);
		if(!contrib.isPresent())
			throw new OSA404Exception(contribNotFoundMsg);

		contribService.denyContrib(contrib.get());
    	return ResponseEntity.ok(new OSAResponseDTO("Contribution refused"));
    }

	@GetMapping(value = "/contrib/{art_id}")
    public ResponseEntity<OSAResponseDTO> getContribs(@PathVariable("art_id") Integer artId) throws OSA404Exception {
		Optional<ArtEntity> art = artRepo.findById(artId);
		if(!art.isPresent())
			throw new OSA404Exception("Art not found.");

		List<ContribEntity> contribs = new ArrayList<>(contribRepo.findByArtId(artId));
		
		return ResponseEntity.ok(
			new OSAResponseDTO(
				contribTransf.modelsToDtos(contribs)
			)
		);
	}
}
