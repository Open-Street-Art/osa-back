package com.osa.openstreetart.server;

import java.io.Serializable;

public class JwtReponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
	private final String jwttoken;

	public JwtReponse(String jwttoken) {
		this.jwttoken = jwttoken;
	}

	public String getToken() {
		return this.jwttoken;
	} 
}
