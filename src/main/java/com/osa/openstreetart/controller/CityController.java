package com.osa.openstreetart.controller;

import java.util.Optional;

import com.osa.openstreetart.dto.OSAResponseDTO;
import com.osa.openstreetart.entity.CityEntity;
import com.osa.openstreetart.exceptions.OSA404Exception;
import com.osa.openstreetart.repository.ArtRepository;
import com.osa.openstreetart.repository.CityRepository;
import com.osa.openstreetart.util.ApiRestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@ApiRestController
public class CityController {
	
	@Autowired
	CityRepository cityRepo;

	@Autowired
	ArtRepository artRepo;

	@GetMapping(value = "/city/{city_id}")
	public ResponseEntity<OSAResponseDTO> getCity(
			@PathVariable("city_id") Integer cityId) throws OSA404Exception {
		
		Optional<CityEntity> city = cityRepo.findById(cityId);
		if (!city.isPresent()) {
			throw new OSA404Exception("City not found.");
		}

		return ResponseEntity.ok(
			new OSAResponseDTO(city.get())
		);
	}

	@GetMapping(value = "/city/arts/{city_id}")
	public ResponseEntity<OSAResponseDTO> getArtsByCity(
			@PathVariable("city_id") Integer cityId) throws OSA404Exception {
		
		Optional<CityEntity> city = cityRepo.findById(cityId);
		if (!city.isPresent()) {
			throw new OSA404Exception("City not found.");
		}

		return ResponseEntity.ok(
			new OSAResponseDTO(artRepo.findByCityId(city.get().getId()))
		);
	}
}
