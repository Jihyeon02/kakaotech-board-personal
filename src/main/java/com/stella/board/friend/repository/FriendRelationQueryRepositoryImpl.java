package com.stella.board.friend.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.stella.board.friend.dto.FriendResponse;
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
            Long ownerId,
            Long friendId
    ) {
        Integer result = queryFactory
                .selectOne()
                .from(friendRelation)
                .where(
                        friendRelation.owner.user_id.eq(ownerId),
                        friendRelation.friend.user_id.eq(friendId)
                )
                .fetchFirst();

        return result != null;
    }

    @Override
    public List<FriendResponse> findFriends(Long ownerId) {
        return queryFactory
                .select(
                        Projections.constructor(
                                FriendResponse.class,
                                friendRelation.friend.user_id,
                                friendRelation.friend.nickname,
                                friendRelation.friend.profile_imageUrl,
                                friendRelation.createdAt
                        )
                )
                .from(friendRelation)
                .where(
                        friendRelation.owner.user_id.eq(ownerId)
                )
                .orderBy(
                        friendRelation.createdAt.desc()
                )
                .fetch();
    }
}