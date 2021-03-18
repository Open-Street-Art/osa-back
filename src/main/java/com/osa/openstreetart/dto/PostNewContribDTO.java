package com.osa.openstreetart.dto;

import lombok.Data;

@Data
public class PostNewContribDTO {
	private String name;
	private String description;
	private String picture1;
	private String picture2;
	private String picture3;
	private Double longitude;
	private Double latitude;
	private String authorName;
}
