package com.osa.openstreetart.dto;

import lombok.Data;

@Data
public class GetContribDTO {
	private Integer id;
	private String name;
	private String description;
	private String picture1;
	private String picture2;
	private String picture3;
	private String author;
	private Integer contributorId;
	private Boolean approved;
}
