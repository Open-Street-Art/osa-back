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

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;

import lombok.Data;

@Data
@Entity
public class UserEntity {

	public static final int USERNAME_MIN_LENGTH = 3;
	public static final int PSW_MIN_LENGTH = 8;

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

	@Lob
	private String profilePicture;

	private Boolean isPublic;
    
	@ElementCollection(targetClass = RoleEnum.class, fetch = FetchType.EAGER)
	@CollectionTable(name="user_roles", joinColumns = @JoinColumn(name = "user_id"))
	@Enumerated(EnumType.STRING)
	@Column(name = "role")
	private Collection<RoleEnum> roles;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
		name="fav_artists",
		joinColumns=@JoinColumn(name="user_id"),
		inverseJoinColumns=@JoinColumn(name="artist_id")
	)
	@JsonBackReference
	private Collection<UserEntity> favArtists;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
		name="fav_cities",
		joinColumns=@JoinColumn(name="user_id"),
		inverseJoinColumns=@JoinColumn(name="city_id")
	)
	@JsonBackReference
	private Collection<CityEntity> favCities;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
		name="fav_arts",
		joinColumns=@JoinColumn(name="user_id"),
		inverseJoinColumns=@JoinColumn(name="art_id")
	)
	@JsonBackReference
	private Collection<ArtEntity> favArts;
}
