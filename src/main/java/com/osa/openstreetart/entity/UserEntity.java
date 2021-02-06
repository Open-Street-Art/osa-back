package com.osa.openstreetart.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class UserEntity {

	@Id
	@GeneratedValue
	private Integer id;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String username;
	
	@Column(nullable = false)
	private String password;
	
	private String description;

	private byte[] profilePicture;

	private boolean isPublic;

	// TODO : Relation OneToMany sur l'enum
	// private List<RoleEnum> roles;

	// TODO : Relation OnetoMany
	// private List<UserEntity> favArtists;
	
    // TODO implémentation des classes  CityEntity, ArtEntity
    //List<CityEntity> favCities
	//List<ArtEntity>favArts;
}
