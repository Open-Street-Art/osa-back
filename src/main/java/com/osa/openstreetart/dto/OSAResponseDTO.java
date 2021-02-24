package com.osa.openstreetart.dto;

import lombok.Data;

@Data
public class OSAResponseDTO {

	private String status = "success";
	private Object data;
	private String message;

	public OSAResponseDTO(final String message) {
		this.message = message;
	}

	public OSAResponseDTO(final Object data) {
		this.data = data;
	}

	public OSAResponseDTO(final Object data, final String message) {
		this.data = data;
		this.message = message;
	}
}
