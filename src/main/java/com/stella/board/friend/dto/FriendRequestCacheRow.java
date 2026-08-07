package com.stella.board.friend.dto;

import java.time.LocalDateTime;

public record FriendRequestCacheRow(
        Long senderId,
        LocalDateTime requestedAt
) {
}