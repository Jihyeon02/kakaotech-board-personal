package com.stella.board.friend.repository;

import com.stella.board.friend.FriendRelation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRelationRepository
        extends JpaRepository<FriendRelation, Long>,
        FriendRelationQueryRepository {
}
