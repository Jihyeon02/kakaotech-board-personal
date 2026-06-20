package com.stella.board.user.auth;

import com.stella.board.global.ErrorCode;
import com.stella.board.user.UserRepository;
import com.stella.board.user.auth.dto.*;
import com.stella.board.global.exception.GlobalExceptionHandler;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.stella.board.user.User;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.HttpClientErrorException;

import javax.security.sasl.AuthenticationException;
import java.time.LocalDateTime;


// 교재에 있는 코드
@Service
@Validated
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final GlobalExceptionHandler exceptionHandler;


    // 로그인
    @Transactional
    public LoginResult login(LoginRequest loginRequest) throws AuthenticationException {

        // 모든 AuthorizedException마다 오류가 떴음 -> 해당 클래스가 존재하지 않았으니까 그래서 만듦 global/exception/AuthorizedException.java
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new AuthenticationException());

        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new AuthenticationException();
        }

        String accessToken = jwtProvider.createAccessToken(
                user.getUser_id(),
                user.getEmail(),
                user.getNickname()
        );

        String refreshToken = jwtProvider.createRefreshToken(user.getUser_id());
        refreshTokenRepository.deleteByUserId(user.getUser_id());
        refreshTokenRepository.save(
                new RefreshToken(
                        refreshToken,
                        user.getUser_id(),
                        LocalDateTime.now().plusDays(14)
                )
        );

        return new LoginResult(
                LoginResponse.of(user, accessToken, jwtProvider.getAccessTokenValidityInMilliseconds()),
                refreshToken
        );
    }

    // 액세스 토큰 재발급
    public TokenResult refreshAccessToken(String refreshToken) {
        RefreshToken saved = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> null);

        if (saved.isExpired()) {
            refreshTokenRepository.delete(saved);
            throw null;
        }

        User user = userRepository.findById(saved.getUserId())
                .orElseThrow(() -> null);

        String newAccessToken = jwtProvider.createAccessToken(
                user.getUser_id(),
                user.getEmail(),
                user.getNickname()
        );

        // Refresh Token 회전 (Rotation)
        String newRefreshToken = jwtProvider.createRefreshToken(user.getUser_id());
        refreshTokenRepository.delete(saved);
        refreshTokenRepository.save(
                new RefreshToken(
                        newRefreshToken,
                        user.getUser_id(),
                        LocalDateTime.now().plusDays(14)
                )
        );

        return new TokenResult(
                new TokenInfo(newAccessToken, 3600),
                newRefreshToken
        );
    }

}
