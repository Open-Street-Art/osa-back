package com.osa.openstreetart.entity;

import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ElementCollection;
import javax.persistence.CollectionTable;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
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
    
	@ElementCollection(targetClass = RoleEnum.class)
	@CollectionTable(name="role_enum")
	@Enumerated(EnumType.STRING)
	private Collection<RoleEnum> roles;

	@OneToMany
	@JoinTable(
		name="fav_artists",
		joinColumns=@JoinColumn(name="user_id"),
		inverseJoinColumns=@JoinColumn(name="artist_id")
	)
	private Collection<UserEntity> favArtists;
	
    // TODO implémentation des classes  CityEntity, ArtEntity
    //List<CityEntity> favCities
	//List<ArtEntity>favArts;
}
