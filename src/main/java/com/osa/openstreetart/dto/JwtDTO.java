package com.osa.openstreetart.dto;

import java.io.Serializable;

public class JwtDTO implements Serializable {

	private static final long serialVersionUID = 1722781797078930499L;
	private final String jwttoken;

	public JwtDTO(String jwttoken) {
		this.jwttoken = jwttoken;
	}

	public String getToken() {
		return this.jwttoken;
	}
	
}
