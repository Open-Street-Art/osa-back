package com.osa.openstreetart.dto;

import java.util.Collection;
import lombok.Data;

@Data
public class UserProfileDTO {
	private Integer id;
	private String username;
	private Collection<String> roles;
	private String profilePicture;
	private String description;
	private Collection<Integer> favArtists;
	private Collection<Integer> favArts;
	private Collection<Integer> favCities;
}
