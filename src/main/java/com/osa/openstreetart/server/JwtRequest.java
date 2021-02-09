package com.osa.openstreetart.server;

import java.io.Serializable;

public class JwtRequest implements Serializable {
    
    private static final long serialVersionUID = 5926468583005150707L;
    private String email;
	private String password;

    public JwtRequest(String email, String password) {
		this.email = email;
		this.password = password;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}   
}
