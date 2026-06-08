package com.stella.board.user.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// 교재에 있던 코드가 돌아가도록 하기 위해 추가된 메소드 :  findByToken(String token), deleteByUserId(Long userId)
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    void deleteByUserId(Long userId);
}