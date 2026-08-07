package com.stella.board.friend.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.stella.board.friend.RequestStatus;
import com.stella.board.friend.dto.FriendRequestCacheRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.stella.board.friend.QFriendApplying.friendApplying;

@Repository
@RequiredArgsConstructor
public class FriendApplyingQueryRepositoryImpl
        implements FriendApplyingQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<FriendRequestCacheRow>
    findWaitingRequestsByReceiverId(Long receiverId) {

        return queryFactory
                .select(
                        Projections.constructor(
                                FriendRequestCacheRow.class,
                                friendApplying.sender.user_id,
                                friendApplying.requestedAt
                        )
                )
                .from(friendApplying)
                .where(
                        friendApplying.receiver.user_id.eq(receiverId),
                        friendApplying.status.eq(
                                RequestStatus.WAITING
                        )
                )
                .orderBy(
                        friendApplying.requestedAt.desc()
                )
                .fetch();
    }
}
