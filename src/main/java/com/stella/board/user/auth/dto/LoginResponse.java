package com.stella.board.user.auth.dto;

import com.stella.board.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
// 교재에 있던 코드
@Getter
@AllArgsConstructor
public class LoginResponse {

    private User user;
    private TokenInfo token;

    public static LoginResponse of(
            User user,
            String accessToken,
            long expiresIn
    ) {
        return new LoginResponse(
                user,
                new TokenInfo(accessToken, expiresIn)
        );
    }
}