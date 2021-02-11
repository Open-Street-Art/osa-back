package com.osa.openstreetart.repository;

import java.util.Optional;

import com.osa.openstreetart.entity.ArtEntity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtRepository extends CrudRepository<ArtEntity, Integer> {
	Optional<ArtEntity> findById(Integer id);
}
