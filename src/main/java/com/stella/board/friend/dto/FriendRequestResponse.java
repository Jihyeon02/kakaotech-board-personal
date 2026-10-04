package com.stella.board.friend.dto;

import java.time.LocalDateTime;

public record FriendRequestResponse(
        Long senderId,
        LocalDateTime requestedAt
) {
}