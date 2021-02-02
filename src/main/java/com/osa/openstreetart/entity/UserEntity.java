package com.osa.openstreetart.entity;

import java.util.List;

public class UserEntity {

    protected Integer id;
    protected String email;
    protected String username;
    protected String password;
    protected String description;
    byte[] profilePicture;
    // TODO implémentation des classes RoleEntity, CityEntity, favArtists, ArtEntity
    //List<RoleEntity> roles;
    //List<CityEntity> favCities
    //List<UserEntity> favArtists
    //List<ArtEntity>favArts;
    boolean isPublic;

    public int getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public String getDescription() {
        return description;
    }
    public byte[] getprofilePicture() {
        return profilePicture.clone();
    }
}