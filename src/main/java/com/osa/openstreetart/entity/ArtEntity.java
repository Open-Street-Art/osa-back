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

import lombok.Data;

@Data
@Entity
public class ArtEntity {
	
	@Id
	@GeneratedValue
	private Integer id;

	@Column(nullable = false)
	private String name;

	private String description;

	@Lob
	@ElementCollection
	@CollectionTable(name="art_pictures", joinColumns = @JoinColumn(name = "art_id"))
	private Collection<byte[]> pictures;

	@ManyToOne
	@JoinColumn(name="artist_id")
	private UserEntity author;

	private String authorName;

	// TODO : a décommenter quand ContribEntity existe
	//private Collection<ContribEntity> contributions;

	@Column(nullable = false)
	private LocalDateTime creationDateTime;

	// TODO : a décommenter quand CityEntity existe
	//private CityEntity city;

	@Column(nullable = false)
	private Double longitude;

	@Column(nullable = false)
	private Double latitude;
}
