package com.stella.board.friend.repository;

import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.stella.board.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.stella.board.friend.QFriendRelation.friendRelation;

@Repository
@RequiredArgsConstructor
public class FriendRelationQueryRepositoryImpl
        implements FriendRelationQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsRelation(
            Long userId1,
            Long userId2
    ) {
        Long userAId = Math.min(userId1, userId2);
        Long userBId = Math.max(userId1, userId2);

        Integer result = queryFactory
                .selectOne()
                .from(friendRelation)
                .where(
                        friendRelation.userA.user_id.eq(userAId),
                        friendRelation.userB.user_id.eq(userBId)
                )
                .fetchFirst();

        return result != null;
    }

    @Override
    public List<User> findFriends(Long userId) {
        return queryFactory
                .select(
                        new CaseBuilder()
                                .when(
                                        friendRelation.userA.user_id
                                                .eq(userId)
                                )
                                .then(friendRelation.userB)
                                .otherwise(friendRelation.userA)
                )
                .from(friendRelation)
                .where(
                        friendRelation.userA.user_id.eq(userId)
                                .or(
                                        friendRelation.userB.user_id
                                                .eq(userId)
                                )
                )
                .fetch();
    }
}
