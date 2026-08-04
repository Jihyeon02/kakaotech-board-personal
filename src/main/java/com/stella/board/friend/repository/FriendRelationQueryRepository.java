package com.stella.board.friend.repository;

import com.stella.board.user.User;

import java.util.List;

public interface FriendRelationQueryRepository {

    boolean existsRelation(
            Long userId1,
            Long userId2
    );

    List<User> findFriends(Long userId);
}
