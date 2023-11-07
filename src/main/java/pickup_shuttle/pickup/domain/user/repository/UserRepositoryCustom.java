package pickup_shuttle.pickup.domain.user.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import pickup_shuttle.pickup.domain.board.Board;
import pickup_shuttle.pickup.domain.user.User;
@Repository
public interface UserRepositoryCustom {
    Slice<User> searchAuthList(Long lastUserId, Pageable pageable);
    Slice<Board> searchRequesterList(Long userId, Long lastBoardId, Pageable pageable);

}
