package com.stella.board.friend.repository;

import com.stella.board.friend.dto.FriendResponse;

import java.util.List;

public interface FriendRelationQueryRepository {

    boolean existsRelation(
            Long ownerId,
            Long friendId
    );

    List<FriendResponse> findFriends(Long ownerId);
}
