package com.stella.board.friend.dto;

import java.time.LocalDateTime;

public record FriendApplyingReadResponse(
        Long applyingId,
        Long senderId,
        LocalDateTime requestedAt

) {
}
