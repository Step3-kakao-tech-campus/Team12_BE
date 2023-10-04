package pickup_shuttle.pickup.domain.board;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBoard is a Querydsl query type for Board
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBoard extends EntityPathBase<Board> {

    private static final long serialVersionUID = 264523342L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBoard board = new QBoard("board");

    public final ListPath<pickup_shuttle.pickup.domain.beverage.Beverage, pickup_shuttle.pickup.domain.beverage.QBeverage> beverages = this.<pickup_shuttle.pickup.domain.beverage.Beverage, pickup_shuttle.pickup.domain.beverage.QBeverage>createList("beverages", pickup_shuttle.pickup.domain.beverage.Beverage.class, pickup_shuttle.pickup.domain.beverage.QBeverage.class, PathInits.DIRECT2);

    public final NumberPath<Long> boardId = createNumber("boardId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath destination = createString("destination");

    public final DateTimePath<java.time.LocalDateTime> finishedAt = createDateTime("finishedAt", java.time.LocalDateTime.class);

    public final BooleanPath isMatch = createBoolean("isMatch");

    public final pickup_shuttle.pickup.domain.match.QMatch match;

    public final StringPath request = createString("request");

    public final pickup_shuttle.pickup.domain.store.QStore store;

    public final NumberPath<Integer> tip = createNumber("tip", Integer.class);

    public final pickup_shuttle.pickup.domain.user.QUser user;

    public QBoard(String variable) {
        this(Board.class, forVariable(variable), INITS);
    }

    public QBoard(Path<? extends Board> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBoard(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBoard(PathMetadata metadata, PathInits inits) {
        this(Board.class, metadata, inits);
    }

    public QBoard(Class<? extends Board> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.match = inits.isInitialized("match") ? new pickup_shuttle.pickup.domain.match.QMatch(forProperty("match"), inits.get("match")) : null;
        this.store = inits.isInitialized("store") ? new pickup_shuttle.pickup.domain.store.QStore(forProperty("store")) : null;
        this.user = inits.isInitialized("user") ? new pickup_shuttle.pickup.domain.user.QUser(forProperty("user")) : null;
    }

}

