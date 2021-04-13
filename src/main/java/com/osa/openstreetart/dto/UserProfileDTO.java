package com.osa.openstreetart.dto;

import java.util.Collection;

import com.osa.openstreetart.entity.ArtEntity;
import com.osa.openstreetart.entity.CityEntity;
import com.osa.openstreetart.entity.UserEntity;

import lombok.Data;

@Data
public class UserProfileDTO {
	private Integer id;
	private String username;
	private Collection<String> roles;
	private String profilePicture;
	private String description;
	private Collection<UserEntity> favArtists;
	private Collection<ArtEntity> favArts;
	private Collection<CityEntity> favCities;
}
