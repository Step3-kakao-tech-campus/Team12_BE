package pickup_shuttle.pickup.domain.board;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepositoryCustom {
    Slice<Board> searchAllBySlice(Long lastBoardId, Pageable pageable);
}
