package com.osa.openstreetart.controller;

import com.osa.openstreetart.dto.ContribDTO;
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
import org.springframework.web.bind.annotation.*;

import javax.naming.PartialResultException;
import java.util.Optional;

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
    @PathVariable("art_id") Integer artId, @RequestBody ContribDTO contrib)
			throws OSA401Exception, OSA404Exception, OSA400Exception {
		if (!jwtService.getRolesByToken(token.substring("Bearer ".length())).contains(RoleEnum.ROLE_USER)) {
			throw new OSA401Exception("Unauthorized.");
		}
		contrib.setId(artId);
		contribService.save(contrib);
		return ResponseEntity.ok(new OSAResponseDTO("Crontribution sent."));
	}

    @DeleteMapping(value = "/admin/art/{art_id}")
	public ResponseEntity<OSAResponseDTO> deleteContrib(@RequestHeader(value = "Authorization") String token,
														@PathVariable("art_id") Integer artId) throws OSA401Exception, OSA404Exception {
		if (!jwtService.getRolesByToken(token.substring("Bearer ".length())).contains(RoleEnum.ROLE_ARTIST)
		|| !jwtService.getRolesByToken(token.substring("Bearer ".length())).contains(RoleEnum.ROLE_ADMIN)) {
			throw new OSA401Exception("Unauthorized.");
		}
		contribService.delete(artId);
		return ResponseEntity.ok(new OSAResponseDTO("Contribution deleted"));
	}

	@PostMapping(value = "/admin/contrib/accept/{contrib_id}")
	public ResponseEntity<OSAResponseDTO> acceptContrib(@RequestHeader(value = "Authorization") String token,
														@PathVariable("contrib_id") Integer contribId)
			throws OSA401Exception,
			OSA404Exception, OSA400Exception {
		if (!jwtService.getRolesByToken(token.substring("Bearer ".length())).contains(RoleEnum.ROLE_ADMIN)) {
			throw new OSA401Exception("Unauthorized.");
		}
		contribService.setArt(contribId, contribRepo.findById(contribId).get());
    	return ResponseEntity.ok(new OSAResponseDTO("Contribution accepted"));
    }

	@PostMapping(value = "/admin/contrib/deny/{contrib_id}")
	public ResponseEntity<OSAResponseDTO> denyContrib(@RequestHeader(value = "Authorization") String token,
														@PathVariable("contrib_id") Integer contribId)
			throws OSA401Exception, OSA404Exception {
		if (!jwtService.getRolesByToken(token.substring("Bearer ".length())).contains(RoleEnum.ROLE_ADMIN)) {
			throw new OSA401Exception("Unauthorized.");
		}
		contribService.delete(contribId);
    	return ResponseEntity.ok(new OSAResponseDTO("Contribution refused"));
    }


}
