package com.osa.openstreetart.errorhandler;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ErrorDto {

	private String status;

	private String message;

	public ErrorDto(final String message) {
		this.status = "error";
		this.message = message;
	}
	
}
