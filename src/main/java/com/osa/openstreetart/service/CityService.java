package com.osa.openstreetart.service;

import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osa.openstreetart.entity.CityEntity;
import com.osa.openstreetart.repository.CityRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class CityService {

	@Autowired
	CityRepository cityRepo;

	// https://positionstack.com/documentation
	private static final String API_KEY = "f7408e47f5c62fc8c992332ed650d3eb";

	public CityEntity getCityFromLatLong(Double lat, Double lng) {
		CityEntity city = new CityEntity();
		String url = "http://api.positionstack.com/v1/reverse?access_key="
			+ API_KEY
			+ "&query="
			+ lat
			+ ","
			+ lng;
		
		RestTemplate restTemplate = new RestTemplate();
		String json;
		try {
			json = restTemplate.getForObject(url, String.class);
		} catch(RestClientException e) {
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			city.setName(mapper.readTree(json).get("data").get(0).get("locality").asText());
		} catch (JsonProcessingException e) {
			return null;
		}
		if (city.getName() == null 
				|| city.getName().isEmpty() 
				|| city.getName().contains("null"))
			city = null;

		return city;
	}

	public CityEntity registerCity(CityEntity city) {
		// On verifie si la ville n'existe pas en base
		Optional<CityEntity> res = cityRepo.findByName(city.getName());
		if (res.isPresent())
			return null;
		// On la sauvegarde puis on retourne l'id de l'entité pour l'associer a l'oeuvre.
		return cityRepo.save(city);
	}

}
