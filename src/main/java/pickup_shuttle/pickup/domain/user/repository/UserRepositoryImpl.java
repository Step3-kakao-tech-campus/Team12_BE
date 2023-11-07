package pickup_shuttle.pickup.domain.user.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import pickup_shuttle.pickup.domain.board.Board;
import pickup_shuttle.pickup.domain.user.User;
import pickup_shuttle.pickup.domain.user.UserRole;

import java.util.List;

import static pickup_shuttle.pickup.domain.board.QBoard.board;
import static pickup_shuttle.pickup.domain.store.QStore.store;
import static pickup_shuttle.pickup.domain.user.QUser.user;

public class UserRepositoryImpl implements UserRepositoryCustom{
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public UserRepositoryImpl(EntityManager em){
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }
    @Override
    public Slice<User> searchAuthList(Long lastUserId, Pageable pageable){
        List<User> content  = queryFactory
                .selectFrom(user)
                .where(
                        gtUserId(lastUserId),
                        user.userRole.eq(UserRole.USER)
                )
                .limit(pageable.getPageSize()+1)
                .fetch();

        boolean hasNext = content.size() > pageable.getPageSize();
        if(hasNext) {
            content.remove(pageable.getPageSize());
        }
        return new SliceImpl<>(content, pageable, hasNext);
    }
    private BooleanExpression gtUserId(Long userId){
        return userId == null? null : user.userId.gt(userId);
    }

    @Override
    public Slice<Board> searchRequesterList(Long userId, Long lastBoardId, Pageable pageable){
        List<Board> content = queryFactory
                .selectFrom(board)
                .join(board.store, store).fetchJoin()
                .where(
                        ltBoardId(lastBoardId),
                        board.user.userId.eq(userId)
                )
                .orderBy(board.boardId.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = content.size() > pageable.getPageSize();
        if(hasNext) {
            content.remove(pageable.getPageSize());
        }
        return new SliceImpl<>(content, pageable, hasNext);
    }
    private BooleanExpression ltBoardId(Long boardId){
        return boardId == null? null : board.boardId.lt(boardId);
    }
}
