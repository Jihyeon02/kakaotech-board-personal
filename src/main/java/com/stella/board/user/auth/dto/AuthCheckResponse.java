package com.stella.board.user.auth.dto;

import com.stella.board.user.User;

public record AuthCheckResponse(
        Long userId,
        String email,
        String nickname,
        String profileImageUrl
) {
    public static AuthCheckResponse from(User user) {
        return new AuthCheckResponse(
                user.getUser_id(),
                user.getEmail(),
                user.getNickname(),
                user.getProfile_imageUrl()
        );
    }
}
