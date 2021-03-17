package com.osa.openstreetart.controller;

import java.util.Optional;

import com.osa.openstreetart.dto.ContribDTO;
import com.osa.openstreetart.dto.OSAResponseDTO;
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
   
    @PostMapping(value = "/contrib/{art_id}")
	public ResponseEntity<OSAResponseDTO> postContrib(@RequestHeader(value = "Authorization") String token,
    @PathVariable("art_id") Integer artId, @RequestBody ContribDTO contrib)
			throws OSA401Exception, OSA400Exception {
		if (!jwtService.getRolesByToken(token.substring("Bearer ".length())).contains(RoleEnum.ROLE_USER)) {
			throw new OSA401Exception("Unauthorized.");
		}

		String username = jwtUtil.getUsernameFromToken(token.substring("Bearer ".length()));
		Optional<UserEntity> contribUser = userRepo.findByUsername(username);
		
		if (!contribUser.isPresent())
			throw new OSA400Exception("No user found.");

		contribService.save(contrib, contribUser.get(), artId);
		return ResponseEntity.ok(new OSAResponseDTO("Crontribution sent."));
	}

    @DeleteMapping(value = "/contrib/{contrib_id}")
	public ResponseEntity<OSAResponseDTO> deleteContrib(@RequestHeader(value = "Authorization") String token,
														@PathVariable("contrib_id") Integer contribId) throws OSA401Exception, OSA404Exception {
		if (!jwtService.getRolesByToken(token.substring("Bearer ".length())).contains(RoleEnum.ROLE_USER)) {
			throw new OSA401Exception("Unauthorized.");
		}
		
		contribService.delete(contribId);
		return ResponseEntity.ok(new OSAResponseDTO("Contribution deleted"));
	}

	@PostMapping(value = "/contrib/accept/{contrib_id}")
	public ResponseEntity<OSAResponseDTO> acceptContrib(@RequestHeader(value = "Authorization") String token,
														@PathVariable("contrib_id") Integer contribId) throws OSA404Exception, OSA401Exception, OSA400Exception {
		if (!jwtService.getRolesByToken(token.substring("Bearer ".length())).contains(RoleEnum.ROLE_ADMIN)) {
			throw new OSA401Exception("Unauthorized.");
		}
		
		Optional<ContribEntity> contrib = contribRepo.findById(contribId);
		if(!contrib.isPresent())
		{
			throw new OSA404Exception("Contribution not found.");
		}
		
		contribService.saveContrib(contrib.get());
    	return ResponseEntity.ok(new OSAResponseDTO("Contribution accepted"));
    }

	@PostMapping(value = "/contrib/deny/{contrib_id}")
	public ResponseEntity<OSAResponseDTO> denyContrib(@RequestHeader(value = "Authorization") String token,
														@PathVariable("contrib_id") Integer contribId)
			throws OSA401Exception, OSA404Exception, OSA400Exception {
		if (!jwtService.getRolesByToken(token.substring("Bearer ".length())).contains(RoleEnum.ROLE_ADMIN)) {
			throw new OSA401Exception("Unauthorized.");
		}
		Optional<ContribEntity> contrib = contribRepo.findById(contribId);
		if(!contrib.isPresent())
		{
			throw new OSA404Exception("Contribution not found.");
		}

		contribService.denyContrib(contrib.get());
    	return ResponseEntity.ok(new OSAResponseDTO("Contribution refused"));
    }

	@GetMapping(value = "/contrib/{art_id}")
    public ResponseEntity<OSAResponseDTO> getContribs(@PathVariable("art_id") Integer artId) throws OSA404Exception{
		Optional<ArtEntity> art = artRepo.findById(artId);
		if(!art.isPresent())
		{
			throw new OSA404Exception("art not found.");
		}
		return ResponseEntity.ok(new OSAResponseDTO(contribRepo.findByArtId(artId)));
	}
}
