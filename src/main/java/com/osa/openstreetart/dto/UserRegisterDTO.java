package com.osa.openstreetart.dto;

import lombok.Data;

@Data
public class UserRegisterDTO {
	private String email;
	private String username;
	private String password;
	private String role;
}
