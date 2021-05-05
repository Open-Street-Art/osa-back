package com.osa.openstreetart.repository;

import com.osa.openstreetart.entity.CityEntity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepository extends CrudRepository<CityEntity, Integer>{

	Optional<CityEntity> findByName(String name);
}
