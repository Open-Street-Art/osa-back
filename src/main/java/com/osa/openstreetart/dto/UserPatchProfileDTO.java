package com.osa.openstreetart.dto;

import lombok.Data;

@Data
public class UserPatchProfileDTO {
	private Boolean isPublic;
	private String profilePicture;
	private String description;
}
