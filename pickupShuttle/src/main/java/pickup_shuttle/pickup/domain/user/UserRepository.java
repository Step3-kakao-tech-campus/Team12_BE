package pickup_shuttle.pickup.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByNickname(String nickname);

    @Query("select u from User u where u.uid = :uid")
    Optional<User> findUserById(@Param("uid") String uid);

}
