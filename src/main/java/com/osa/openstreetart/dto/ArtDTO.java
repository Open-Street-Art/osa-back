package com.osa.openstreetart.dto;

import lombok.Data;

@Data
public class ArtDTO {
	private String name;
	private String description;
	private String picture1;
	private String picture2;
	private String picture3;	
	private String author;
	private Integer author_id;
	private Integer city_id;
	private Double longitude;
	private Double latitude;
}
