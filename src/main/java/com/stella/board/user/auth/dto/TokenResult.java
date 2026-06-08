package com.stella.board.user.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 교재에 있던 코드
@Getter
@AllArgsConstructor
public class TokenResult {
    private TokenInfo token;        // 응답 바디 (accessToken, expiresIn)
    private String newRefreshToken;     // 회전 시에만 사용 (없으면 null)
}