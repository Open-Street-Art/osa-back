package com.osa.openstreetart.controller;


import com.osa.openstreetart.dto.ArtDTO;
import com.osa.openstreetart.dto.EditArtDTO;
import com.osa.openstreetart.dto.OSAResponseDTO;
import com.osa.openstreetart.exceptions.OSA400Exception;
import com.osa.openstreetart.exceptions.OSA404Exception;
import com.osa.openstreetart.service.ArtService;
import com.osa.openstreetart.service.CityService;
import com.osa.openstreetart.service.JwtService;
import com.osa.openstreetart.tranformator.ArtLocationTransformator;
import com.osa.openstreetart.util.ApiRestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@ApiRestController
public class ArtController {

	@Autowired
	JwtService jwtService;

	@Autowired
	ArtService artService;

	@Autowired
	CityService cityService;

	@Autowired
	ArtLocationTransformator artTransf;

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping(value = "/admin/arts")
	public ResponseEntity<OSAResponseDTO> postArt(@RequestHeader(value = "Authorization") String token,
			@RequestBody ArtDTO art) throws OSA400Exception {

		artService.save(art);
		return ResponseEntity.ok(new OSAResponseDTO("Art created."));
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PatchMapping(value = "/admin/arts/{art_id}")
	public ResponseEntity<OSAResponseDTO> patchArt(@RequestHeader(value = "Authorization") String token,
			@PathVariable("art_id") Integer artId, @RequestBody EditArtDTO art)
			throws OSA404Exception, OSA400Exception {

		artService.patch(artId, art);
		return ResponseEntity.ok(new OSAResponseDTO("Art modified."));
	}

	@GetMapping(value = "/arts/locations")
	public ResponseEntity<OSAResponseDTO> getArtsLocations() {
		
		return ResponseEntity.ok(new OSAResponseDTO(
			artTransf.modelsToDtos(artService.findAll()))
		);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping(value = "/admin/arts/{art_id}")
	public ResponseEntity<OSAResponseDTO> deleteArt(@RequestHeader(value = "Authorization") String token,
			@PathVariable("art_id") Integer artId) throws OSA404Exception {
		
		artService.delete(artId);
		return ResponseEntity.ok(new OSAResponseDTO("Art deleted"));
	}

	@GetMapping(value = "/arts/{art_id}")
	public ResponseEntity<OSAResponseDTO> getArt(@PathVariable("art_id") Integer artId) {
		return ResponseEntity.ok(new OSAResponseDTO(artService.getArt(artId)));
	}
}
