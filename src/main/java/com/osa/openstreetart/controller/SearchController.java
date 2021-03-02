package com.osa.openstreetart.controller;

import com.osa.openstreetart.dto.OSAResponseDTO;
import com.osa.openstreetart.exceptions.OSA400Exception;
import com.osa.openstreetart.repository.ArtRepository;
import com.osa.openstreetart.util.ApiRestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@ApiRestController
public class SearchController {
	
	@Autowired
	private ArtRepository artRepo;

	@GetMapping(value = "/search/arts/{content}")
	public ResponseEntity<OSAResponseDTO> getSearchArts(@PathVariable("content") String content)
			throws OSA400Exception {
		if (content.isEmpty() || content.isBlank())
			throw new OSA400Exception("Empty search content.");

		return ResponseEntity.ok(
			new OSAResponseDTO(artRepo.findByNameWithSub(content))
		);
	}
}
