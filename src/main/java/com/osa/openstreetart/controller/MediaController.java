package com.osa.openstreetart.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.lowagie.text.DocumentException;
import com.osa.openstreetart.exceptions.OSA404Exception;
import com.osa.openstreetart.service.MediaService;
import com.osa.openstreetart.util.ApiRestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@ApiRestController
public class MediaController {

	@Autowired
	private MediaService mediaService;

	@GetMapping("/media/csv/{city_id}")
	public void getArtsOfCityInCSV(HttpServletResponse response,
			@PathVariable("city_id") Integer cityId) throws IOException {
		mediaService.sendCSV(response, cityId);
	}

	@GetMapping("/media/pdf/{city_id}")
	public HttpEntity<byte[]> getArtsOfCityInPDF(HttpServletResponse response,
			@PathVariable("city_id") Integer cityId) throws DocumentException, IOException, OSA404Exception {
		return mediaService.sendPDF(cityId);
	}

}
