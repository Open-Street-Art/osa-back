package com.osa.openstreetart.dto;

import java.util.Collection;
import lombok.Data;

@Data
public class ContribDTO {
    private String name;
	private String description;
	private Collection<String> pictures;
}
