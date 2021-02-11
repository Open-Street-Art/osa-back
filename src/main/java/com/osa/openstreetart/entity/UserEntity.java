package com.osa.openstreetart.entity;

import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
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

	static public final int USERNAME_MIN_LENGTH = 3;
	static public final int PSW_MIN_LENGTH = 8;

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
    
	@ElementCollection(targetClass = RoleEnum.class, fetch = FetchType.EAGER)
	@CollectionTable(name="user_roles", joinColumns = @JoinColumn(name = "user_id"))
	@Enumerated(EnumType.STRING)
	@Column(name = "role")
	private Collection<RoleEnum> roles;

	@OneToMany(fetch = FetchType.EAGER)
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
