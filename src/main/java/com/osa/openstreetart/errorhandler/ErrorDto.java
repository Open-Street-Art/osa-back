package com.osa.openstreetart.errorhandler;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ErrorDto {

	private String errorCode;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
	private LocalDateTime dateTime;

	public ErrorDto(final String errorCode, final LocalDateTime dateTime) {
		this.errorCode = errorCode;
		this.dateTime = dateTime;
	}
	
}
