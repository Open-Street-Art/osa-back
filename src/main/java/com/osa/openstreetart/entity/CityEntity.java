package com.osa.openstreetart.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
}
