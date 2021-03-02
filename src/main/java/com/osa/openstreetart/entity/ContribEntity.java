package com.osa.openstreetart.entity;

import java.time.LocalDateTime;
import java.util.Collection;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;

import lombok.Data;

@Data
@Entity
public class ContribEntity {
    
    @Id
	@GeneratedValue
    private Integer Id;
    
    @Column( nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String description;

    @ManyToOne
	@JoinColumn(name="art_id", nullable=false)
	private ArtEntity art;

    @Type(type = "org.hibernate.type.TextType")
	@ElementCollection
	@LazyCollection(LazyCollectionOption.FALSE)
	@CollectionTable(name="contrib_pictures", joinColumns = @JoinColumn(name = "Contrib_id"))
	private Collection<String> pictures;

    @ManyToOne
	@JoinColumn(name="contributor_id")
	private UserEntity contributor;

	private String authorName;

    @Column(nullable = false)
	private LocalDateTime creationDateTime;

    @ManyToOne
	@JoinColumn(name="city_id")
	private CityEntity city;

    @Column(nullable = false)
	private Double longitude;

	@Column(nullable = false)
	private Double latitude;

    private Boolean approved;
}
