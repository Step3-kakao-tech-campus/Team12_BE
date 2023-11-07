package pickup_shuttle.pickup.domain.match;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {
    @Query("select m from Match m join fetch m.user where m.matchId = :matchId")
    Optional<Match> mfindByMatchId(@Param("matchId") Long matchId);
}
