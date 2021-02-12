package com.osa.openstreetart.dto;

import java.time.LocalDateTime;

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
	private LocalDateTime creationDateTime;
	private Double longitude;
	private Double latitude;
}
