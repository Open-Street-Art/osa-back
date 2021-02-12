package com.osa.openstreetart.dto;

import java.util.Collection;

import lombok.Data;

@Data
public class UserProfileDTO {
	private Integer id;
	private String username;
	private String roles;
	private String profilePicture;
	private String description;
	private Collection<Integer> favArtists;
}
