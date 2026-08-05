package com.stella.board.friend.dto;

import java.time.LocalDateTime;

public record FriendResponse(
        Long friendId,
        String nickname,
        String profileImageUrl,
        LocalDateTime createdAt
) {
}