package pickup_shuttle.pickup.domain.beverage;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBeverage is a Querydsl query type for Beverage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBeverage extends EntityPathBase<Beverage> {

    private static final long serialVersionUID = 367755710L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBeverage beverage = new QBeverage("beverage");

    public final NumberPath<Long> beverageId = createNumber("beverageId", Long.class);

    public final pickup_shuttle.pickup.domain.board.QBoard board;

    public final StringPath name = createString("name");

    public QBeverage(String variable) {
        this(Beverage.class, forVariable(variable), INITS);
    }

    public QBeverage(Path<? extends Beverage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBeverage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBeverage(PathMetadata metadata, PathInits inits) {
        this(Beverage.class, metadata, inits);
    }

    public QBeverage(Class<? extends Beverage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.board = inits.isInitialized("board") ? new pickup_shuttle.pickup.domain.board.QBoard(forProperty("board"), inits.get("board")) : null;
    }

}

