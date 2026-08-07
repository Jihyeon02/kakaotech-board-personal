package com.stella.board.user.auth;

import lombok.Getter;

import java.security.Principal;

@Getter
public final class AuthenticatedUser implements Principal {

    private final Long userId;
    private final String email;
    private final String nickname;

    public AuthenticatedUser(
            Long userId,
            String email,
            String nickname
    ) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
    }

    @Override
    public String getName() {
        return String.valueOf(userId);
    }
}
