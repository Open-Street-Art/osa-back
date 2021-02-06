package com.osa.openstreetart.controller;

import java.util.List;

public class UserDTO {

    private String email;
    private String username;
    private String password;
    private List<String> roles = new ArrayList<String>();
    
    public String getEmail() {
        return email;     
    }

    public void setEmail(String email) {
        this.email = email;       
    }

    public String getUsername() {
        return username;        
    }

    public void setUsername(String username) {
        this.username = username;       
    }

    public String getPassword() {
        return password;        
    }

    public void setPassword(String password) {
        this.password = password;       
    }

    public List<String> getRole() {
        return roles;        
    }

    public void setRole(List<String> roles) {
        this.roles = roles;        
    }
}
