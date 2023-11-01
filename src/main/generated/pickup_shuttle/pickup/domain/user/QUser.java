package pickup_shuttle.pickup.domain.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 2115563710L;

    public static final QUser user = new QUser("user");

    public final StringPath account = createString("account");

    public final StringPath bank = createString("bank");

    public final StringPath email = createString("email");

    public final StringPath name = createString("name");

    public final StringPath nickname = createString("nickname");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final StringPath pwd = createString("pwd");

    public final StringPath socialId = createString("socialId");

    public final StringPath uid = createString("uid");

    public final StringPath url = createString("url");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final EnumPath<UserRole> userRole = createEnum("userRole", UserRole.class);

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

