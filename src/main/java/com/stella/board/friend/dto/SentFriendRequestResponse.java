package com.stella.board.friend.dto;

import java.time.LocalDateTime;

public record SentFriendRequestResponse(
        Long requestId,
        Long receiverId,
        String receiverNickname,
        LocalDateTime requestedAt
) {
}
