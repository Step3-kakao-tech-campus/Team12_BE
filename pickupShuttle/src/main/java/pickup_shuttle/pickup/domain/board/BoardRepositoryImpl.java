//package pickup_shuttle.pickup.domain.board;
//
//import com.querydsl.core.types.dsl.BooleanExpression;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import jakarta.persistence.EntityManager;
//import org.springframework.context.annotation.Primary;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Slice;
//import org.springframework.data.domain.SliceImpl;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//@Primary
//public class BoardRepositoryImpl implements BoardRepositoryCustom {
//
//    private final JPAQueryFactory query;
//    private final EntityManager em;
//
//    public BoardRepositoryImpl(EntityManager em) {
//        this.em = em;
//        this.query = new JPAQueryFactory(em);
//    }
//
//    @Override
//    public Slice<Board> searchByTagAndSlice(Long lastBoardId, String tag, Pageable pageable) {
//        List<Board> results = query
//                .selectFrom(QBoard.board)
//                .join(QBoardTag.boardTag).on(QBoard.board.boardId.eq(QBoardTag.boardTag.board.boardId)) //공통필드 BoardId
//                .join(QTag.tag).on(QBoardTag.boardTag.tag.tagId.eq(QTag.tag.tagId)) //공통필드 TagId
//                .where(QTag.tag.name.like("%"+tag+"%"), ltBoardId(lastBoardId))
//                .orderBy(QBoard.board.boardId.desc()) //최신글부터 보여지기
//                .limit(pageable.getPageSize() + 1)
//                .fetch();
//
//
//        boolean hasNext = results.size() > pageable.getPageSize();
//        if (hasNext) {
//            results.remove(pageable.getPageSize());
//
//        }
//
//        return new SliceImpl<>(results, pageable, hasNext);
//    }
//
//    @Override
//    public Slice<Board> searchAllBySlice(Long lastBoardId, Pageable pageable) {
//        List<Board> results = query
//                .selectFrom(QBoard.board)
//                .where(ltBoardId(lastBoardId))
//                .orderBy(QBoard.board.boardId.desc()) //최신글부터 보여지기
//                .limit(pageable.getPageSize() + 1)
//                .fetch();
//
//        boolean hasNext = results.size() > pageable.getPageSize();
//        if (hasNext) {
//            results.remove(pageable.getPageSize());
//
//        }
//
//        return new SliceImpl<>(results, pageable, hasNext);
//    }
//
//
//    private BooleanExpression ltBoardId(Long boardId) {
//        if (boardId == null) {
//            return null;
//        }
//        return QBoard.board.boardId.lt(boardId);
//    }
//}
//
//
