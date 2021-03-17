package com.osa.openstreetart.repository;

import java.util.Collection;
import java.util.Optional;
import com.osa.openstreetart.entity.ContribEntity;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContribRepository extends CrudRepository<ContribEntity, Integer> {
	Optional<ContribEntity> findById(Integer id);
	Optional<ContribEntity> findByName(String name);
	Optional<ContribEntity> findByContributor(String contributor);
	
	@Query("select c from ContribEntity c where c.art.id = ?1")
	Collection<ContribEntity> findByArtId(Integer artId);
}
