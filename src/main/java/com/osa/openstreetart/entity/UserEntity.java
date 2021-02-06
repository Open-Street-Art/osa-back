package com.osa.openstreetart.entity;

import java.util.List;
import lombok.Data;

@Data
public class UserEntity {

    private Integer id;
    private String email;
    private String username;
    private String password;
	private String description;
	private byte[] profilePicture;
	private boolean isPublic;
	private List<RoleEnum> roles;
	private List<UserEntity> favArtists;
	
    // TODO implémentation des classes  CityEntity, ArtEntity
    //List<CityEntity> favCities
	//List<ArtEntity>favArts;
}
