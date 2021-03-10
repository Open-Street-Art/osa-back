package com.osa.openstreetart.dto;

import java.time.LocalDateTime;
import java.util.Collection;

import com.osa.openstreetart.entity.ArtEntity;
import com.osa.openstreetart.entity.CityEntity;
import com.osa.openstreetart.entity.UserEntity;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Data
public class ContribDTO {
	private Integer Id;
    private String name;
	private String description;
	private ArtEntity art;
	private Collection<String> pictures;
	private UserEntity contributor;
	private String authorName;
	private Boolean approved;
}
