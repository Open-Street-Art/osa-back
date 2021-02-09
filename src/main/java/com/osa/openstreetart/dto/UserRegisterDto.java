package com.osa.openstreetart.dto;

import lombok.Data;

import com.osa.openstreetart.entity.RoleEnum;

@Data
public class UserRegisterDto {
    private String email;
    private String username;
    private String password;
    private RoleEnum   role;
}
