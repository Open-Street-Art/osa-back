package com.osa.openstreetart.controller;

import com.osa.openstreetart.dto.ArtDTO;
import com.osa.openstreetart.entity.RoleEnum;
import com.osa.openstreetart.exceptions.OSA400Exception;
import com.osa.openstreetart.exceptions.OSA401Exception;
import com.osa.openstreetart.exceptions.OSA404Exception;
import com.osa.openstreetart.service.ArtService;
import com.osa.openstreetart.service.JwtService;
import com.osa.openstreetart.util.ApiRestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@ApiRestController
public class ArtController {

	@Autowired
	JwtService jwtService;

	@Autowired
	ArtService artService;

	@PatchMapping(value = "/art/{art_id}")
	public ResponseEntity<String> patchArt(@RequestHeader(value = "Authorization") String token,
			@PathVariable("art_id") Integer artId, @RequestBody ArtDTO art)
			throws OSA401Exception, OSA404Exception, OSA400Exception {
		if (!jwtService.getRolesByToken(token.substring("Bearer ".length())).contains(RoleEnum.ROLE_ADMIN))
			throw new OSA401Exception("Unauthorized.");

		artService.patch(artId, art);
		return ResponseEntity.ok("Art modified.");
	}

}
