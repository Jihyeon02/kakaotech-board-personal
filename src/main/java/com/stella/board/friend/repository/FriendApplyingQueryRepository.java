package com.stella.board.friend.repository;

import com.stella.board.friend.dto.FriendRequestCacheRow;

import java.util.List;

public interface FriendApplyingQueryRepository {

    List<FriendRequestCacheRow> findWaitingRequestsByReceiverId(
            Long receiverId
    );
}