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
import pickup_shuttle.pickup.domain.board.repository.BoardRepositoryCustom;

import java.util.List;
import static pickup_shuttle.pickup.domain.board.QBoard.*;
import static pickup_shuttle.pickup.domain.store.QStore.*;

@Repository
@Primary
public class BoardRepositoryImpl implements BoardRepositoryCustom {

    private final JPAQueryFactory query;
    private final EntityManager em;

    public BoardRepositoryImpl(EntityManager em) {
        this.em = em;
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
}


