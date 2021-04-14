package com.osa.openstreetart.entity;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;

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
	@JsonBackReference
	private Collection<ArtEntity> arts;
}
