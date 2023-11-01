package pickup_shuttle.pickup.domain.user.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import pickup_shuttle.pickup.domain.user.User;
import pickup_shuttle.pickup.domain.user.UserRole;

import java.util.List;

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
                        ltUserId(lastUserId),
                        user.url.isNotEmpty(),
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

    private BooleanExpression ltUserId(Long userId){
        return userId == null? null : user.userId.lt(userId);
    }
}
