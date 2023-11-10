package pickup_shuttle.pickup.domain.board.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import pickup_shuttle.pickup.domain.board.Board;
import java.util.List;
import static pickup_shuttle.pickup.domain.board.QBoard.board;
import static pickup_shuttle.pickup.domain.match.QMatch.match;
import static pickup_shuttle.pickup.domain.store.QStore.store;

@Repository
@Primary
public class BoardRepositoryImpl implements BoardRepositoryCustom {

    private final JPAQueryFactory query;

    public BoardRepositoryImpl(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public Slice<Board> searchAllBySlice(Long lastBoardId, Pageable pageable) {
        List<Board> results = query
                .selectFrom(board)
                .join(board.store, store).fetchJoin()
                .where(ltBoardId(lastBoardId))
                .orderBy(board.boardId.desc()) //최신글부터 보여지기
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = results.size() > pageable.getPageSize();
        if (hasNext) {
            results.remove(pageable.getPageSize());

        }

        return new SliceImpl<>(results, pageable, hasNext);
    }


    private BooleanExpression ltBoardId(Long boardId) {
        if (boardId == null) {
            return null;
        }
        return board.boardId.lt(boardId);
    }

    @Override
    public Slice<Board> searchAllBySlice2(Long lastBoardId, Pageable pageable, Long userId) {
        List<Board> results = query
                .selectFrom(board)
                .join(board.store, store).fetchJoin()
                .join(board.match, match).fetchJoin()
                .where(ltBoardId(lastBoardId),
                        match.user.userId.eq(userId))
                .orderBy(board.boardId.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = results.size() > pageable.getPageSize();
        if (hasNext) {
            results.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }
}


