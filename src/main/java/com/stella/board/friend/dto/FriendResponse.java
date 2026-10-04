package com.stella.board.friend.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public record FriendResponse(
        Long friendId,
        String nickname,
        String profileImageUrl,
        LocalDateTime createdAt
) implements Serializable { // 직렬화를 고려해서 추가
}