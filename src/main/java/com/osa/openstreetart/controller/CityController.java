package com.osa.openstreetart.controller;


import com.osa.openstreetart.dto.OSAResponseDTO;
import com.osa.openstreetart.exceptions.OSA404Exception;
import com.osa.openstreetart.service.ArtService;
import com.osa.openstreetart.service.CityService;
import com.osa.openstreetart.util.ApiRestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@ApiRestController
public class CityController {
	
	@Autowired
	CityService cityService;

	@Autowired
	ArtService artService;

	@GetMapping(value = "/cities/{city_id}")
	public ResponseEntity<OSAResponseDTO> getCity(
			@PathVariable("city_id") Integer cityId) throws OSA404Exception {
		return ResponseEntity.ok(
			new OSAResponseDTO(cityService.getOrFail(cityId))
		);
	}

	@GetMapping(value = "/cities/{city_id}/arts")
	public ResponseEntity<OSAResponseDTO> getArtsByCity(
			@PathVariable("city_id") Integer cityId) throws OSA404Exception {
		
		return ResponseEntity.ok(
			new OSAResponseDTO(
				artService.findByCityId(cityService.getOrFail(cityId).getId())
			)
		);
	}
}
