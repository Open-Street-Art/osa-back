package com.osa.openstreetart.service;

import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.osa.openstreetart.entity.CityEntity;
import com.osa.openstreetart.exceptions.OSA404Exception;
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
		var city = new CityEntity();
		String url = "http://api.positionstack.com/v1/reverse?access_key="
			+ API_KEY
			+ "&query="
			+ lat
			+ ","
			+ lng;
		
		var restTemplate = new RestTemplate();
		String json;
		try {
			json = restTemplate.getForObject(url, String.class);
		} catch(RestClientException e) {
			return null;
		}
		var mapper = new ObjectMapper();
		try {
			city
				.setName(mapper.readTree(json)
				.get("data")
				.get(0)
				.get("locality")
				.asText());
		} catch (Exception e) {
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
			return res.get();
		// On la sauvegarde puis on retourne l'id de l'entité pour l'associer a l'oeuvre.
		return cityRepo.save(city);
	}

	public CityEntity getOrFail(Integer cityId) throws OSA404Exception{
		Optional<CityEntity> city = cityRepo.findById(cityId);
		if (city.isEmpty()) {
			throw new OSA404Exception("City not found.");
		}
		
		return city.get();
	}

}
