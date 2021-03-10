package com.osa.openstreetart.repository;

import java.util.Collection;
import java.util.Optional;

import com.osa.openstreetart.entity.ArtEntity;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtRepository extends CrudRepository<ArtEntity, Integer> {
	
	Optional<ArtEntity> findById(Integer id);

	Optional<ArtEntity> findByName(String name);

	Collection<ArtEntity> findByAuthorId(Integer id);

	@Query("select a from ArtEntity a where lower(a.name) like lower(concat('%', concat(?1, '%')))")
	Collection<ArtEntity> findByNameWithSub(String substring);

	@Query("select a from ArtEntity a where lower(a.authorName) like lower(concat('%', concat(?1, '%')))")
	Collection<ArtEntity> findByAuthorNameWithSub(String substring);

	@Query("select a from ArtEntity a where a.city.id in (select c.id from CityEntity c where lower(c.name) like lower(concat('%', concat(?1, '%'))))")
	Collection<ArtEntity> findByCitiesName(String content);
}
