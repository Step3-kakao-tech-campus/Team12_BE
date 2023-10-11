package pickup_shuttle.pickup.domain.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pickup_shuttle.pickup.domain.board.Board;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query("select b from Board b join fetch b.store where b.boardId = :boardId")
    Optional<Board> mfindByBoardId(@Param("boardId") Long boardId);
    @Query("select b from Board b join fetch b.store join fetch b.match where b.boardId =:boardId")
    Optional<Board> m2findByBoardId(@Param("boardId") Long boardId);
    @Query("select b from Board b join fetch b.beverages where b.boardId =:boardId")
    Optional<Board> m3findByBoardId(@Param("boardId") Long boardId);
}
