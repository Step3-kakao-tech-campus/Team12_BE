package pickup_shuttle.pickup.domain.board.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import pickup_shuttle.pickup.domain.board.Board;

@Repository
public interface BoardRepositoryCustom {
    Slice<Board> searchAllBySlice(Long lastBoardId, Pageable pageable);
}
