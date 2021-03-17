package com.osa.openstreetart.dto;

import lombok.Data;

@Data
public class PostContribDTO {
    private String name;
	private String description;
	private String picture1;
	private String picture2;
	private String picture3;
	private Integer contributor_id;
}
