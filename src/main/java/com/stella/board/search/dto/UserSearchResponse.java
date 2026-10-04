package com.stella.board.search.dto;

public record UserSearchResponse(
        Long userId,
        String nickname,
        String profileImageUrl
) {
}
