package pickup_shuttle.pickup.domain.match;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMatch is a Querydsl query type for Match
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMatch extends EntityPathBase<Match> {

    private static final long serialVersionUID = -660324148L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMatch match = new QMatch("match");

    public final NumberPath<Integer> arrivalTime = createNumber("arrivalTime", Integer.class);

    public final pickup_shuttle.pickup.domain.board.QBoard board;

    public final NumberPath<Long> matchId = createNumber("matchId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> matchTime = createDateTime("matchTime", java.time.LocalDateTime.class);

    public final pickup_shuttle.pickup.domain.QUser user;

    public QMatch(String variable) {
        this(Match.class, forVariable(variable), INITS);
    }

    public QMatch(Path<? extends Match> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMatch(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMatch(PathMetadata metadata, PathInits inits) {
        this(Match.class, metadata, inits);
    }

    public QMatch(Class<? extends Match> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.board = inits.isInitialized("board") ? new pickup_shuttle.pickup.domain.board.QBoard(forProperty("board"), inits.get("board")) : null;
        this.user = inits.isInitialized("user") ? new pickup_shuttle.pickup.domain.QUser(forProperty("user")) : null;
    }

}

