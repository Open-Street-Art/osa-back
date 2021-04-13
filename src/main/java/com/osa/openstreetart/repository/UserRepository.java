package com.osa.openstreetart.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

import javax.transaction.Transactional;

import com.osa.openstreetart.entity.UserEntity;

@Repository
@Transactional
public interface UserRepository extends CrudRepository<UserEntity, Integer> {

	Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUsername(String username);

	@Query("select u from UserEntity u where lower(u.username) like lower(concat('%', concat(?1, '%')))")
	Collection<UserEntity> findByUsernameWithSub(String substring);
}
