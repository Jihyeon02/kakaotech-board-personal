package com.stella.board.user.auth;

import com.stella.board.global.response.ApiResponse;
import com.stella.board.user.User;
import com.stella.board.user.UserRepository;
import com.stella.board.user.auth.dto.AuthCheckResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthCheckController {

    private final UserRepository userRepository;

    @GetMapping("/check")
    public ResponseEntity<ApiResponse<AuthCheckResponse>> check(
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        User currentUser = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "인증된 사용자를 찾을 수 없습니다."
                ));

        return ResponseEntity.ok(
                ApiResponse.of(
                        "AUTH_CHECK_SUCCESS",
                        AuthCheckResponse.from(currentUser)
                )
        );
    }
}
