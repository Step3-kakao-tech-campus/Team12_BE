package pickup_shuttle.pickup.domain.refreshToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    @Query("select r from RefreshToken r where r.user.email = :email")
    Optional<RefreshToken> findByAccountEmail(@Param("email") String email);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);

}
