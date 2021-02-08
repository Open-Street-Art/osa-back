package  com.osa.openstreetart.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.osa.openstreetart.entity.UserEntity;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Integer> {
	UserEntity findByEmail(String email);
    UserEntity findByUsername(String username);
}
