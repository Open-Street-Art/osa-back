package com.osa.openstreetart.entity;

import java.time.LocalDateTime;
import java.util.Collection;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;


import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;

import lombok.Data;


@Data
@Entity
public class ArtEntity {
	
	public static final int NAME_MIN_LENGTH = 3;

	@Id
	@GeneratedValue
	private Integer id;

	@Column(nullable = false)
	private String name;

	private String description;

	// Soucis de désérialisation en format BLOB, le DTO accueillant des string base64
	// ce n'est pas génant de laisser en String et de stocker le base64 en BDD
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@ElementCollection
	@LazyCollection(LazyCollectionOption.FALSE)
	@CollectionTable(name="art_pictures", joinColumns = @JoinColumn(name = "art_id"))
	private Collection<String> pictures;

	@ManyToOne
	@JoinColumn(name="artist_id")
	private UserEntity author;

	private String authorName;

	@OneToMany(targetEntity=ContribEntity.class, mappedBy="art")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Collection<ContribEntity> contributions;

	@Column(nullable = false)
	private LocalDateTime creationDateTime;

	@ManyToOne
	@JoinColumn(name="city_id")
	private CityEntity city;

	@Column(nullable = false)
	private Double longitude;

	@Column(nullable = false)
	private Double latitude;
}
