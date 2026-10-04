package com.stella.board.friend.event;

import java.time.LocalDateTime;

public record FriendRequestCreatedEvent(
        Long receiverId,
        Long senderId,
        LocalDateTime requestedAt
) {
}