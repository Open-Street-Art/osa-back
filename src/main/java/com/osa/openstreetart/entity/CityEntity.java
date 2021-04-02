package com.osa.openstreetart.entity;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.GeneratedValue;

import lombok.Data;

@Data
@Entity
public class CityEntity {

    @Id
	@GeneratedValue
    private Integer id;

    @Column(nullable = false)
    private String name;

	@OneToMany(mappedBy="city")
	@JsonIdentityInfo(generator= ObjectIdGenerators.UUIDGenerator.class, property="uuid")
	private Collection<ArtEntity> arts;
}
