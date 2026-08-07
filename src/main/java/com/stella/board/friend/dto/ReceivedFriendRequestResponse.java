package com.stella.board.friend.dto;

import java.time.LocalDateTime;

public record ReceivedFriendRequestResponse(
        Long requestId,
        Long senderId,
        String senderNickname,
        LocalDateTime requestedAt
) {
}
