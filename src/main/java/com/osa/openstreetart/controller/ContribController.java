package com.osa.openstreetart.controller;

import java.util.ArrayList;
import java.util.List;

import com.osa.openstreetart.dto.EditArtDTO;
import com.osa.openstreetart.dto.OSAResponseDTO;
import com.osa.openstreetart.entity.ContribEntity;
import com.osa.openstreetart.entity.RoleEnum;
import com.osa.openstreetart.exceptions.OSA400Exception;
import com.osa.openstreetart.exceptions.OSA401Exception;
import com.osa.openstreetart.exceptions.OSA404Exception;
import com.osa.openstreetart.repository.ContribRepository;
import com.osa.openstreetart.service.ContribService;
import com.osa.openstreetart.service.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public class ContribController {

	@Autowired
	JwtService jwtService;

	@Autowired
	ContribService contribService;

	@Autowired
	ContribRepository contribRepo;
	
    @GetMapping(value = "/contrib/{art_id}")
    public ResponseEntity<OSAResponseDTO> getContribs(@PathVariable("art_id") Integer artId) {
		return ResponseEntity.ok(new OSAResponseDTO(contribRepo.findById(artId)));
	}

    @PostMapping(value = "/user/contrib/{art_id}")
	public ResponseEntity<OSAResponseDTO> postContrib(@RequestHeader(value = "Authorization") String token,
    @PathVariable("art_id") Integer artId, @RequestBody EditArtDTO contrib)
			throws OSA401Exception, OSA400Exception {
		if (!jwtService.getRolesByToken(token.substring("Bearer ".length())).contains(RoleEnum.ROLE_USER)) {
			throw new OSA401Exception("Unauthorized.");
		}
		contribService.save(contrib);
		return ResponseEntity.ok(new OSAResponseDTO("Crontribution sent."));
	}

    @DeleteMapping(value = "/admin/art/{art_id}")
	public ResponseEntity<OSAResponseDTO> deleteArt(@RequestHeader(value = "Authorization") String token,
			@PathVariable("art_id") Integer artId) throws OSA401Exception, OSA404Exception {
		if (!jwtService.getRolesByToken(token.substring("Bearer ".length())).contains(RoleEnum.ROLE_ADMIN)) {
			throw new OSA401Exception("Unauthorized.");
		}
		contribService.delete(artId);
		return ResponseEntity.ok(new OSAResponseDTO("Art deleted"));
	}
}
