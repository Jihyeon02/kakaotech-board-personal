package com.stella.board.user.auth;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class JwtAuthenticationFilterTest {

    private JwtProvider jwtProvider;
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("test-secret-key-must-be-at-least-32-bytes");
        properties.setAccessTokenExpSeconds(600);
        properties.setRefreshTokenExpSeconds(1200);

        jwtProvider = new JwtProvider(properties);
        jwtProvider.init();
        filter = new JwtAuthenticationFilter(jwtProvider);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void accessToken을_인증_사용자로_변환한다()
            throws ServletException, IOException {
        String accessToken = jwtProvider.createAccessToken(
                7L,
                "user@example.com",
                "stella"
        );
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(
                HttpHeaders.AUTHORIZATION,
                "Bearer " + accessToken
        );
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        AuthenticatedUser principal = (AuthenticatedUser) authentication
                .getPrincipal();

        assertThat(chain.getRequest()).isNotNull();
        assertThat(authentication.isAuthenticated()).isTrue();
        assertThat(principal.getUserId()).isEqualTo(7L);
        assertThat(principal.getEmail()).isEqualTo("user@example.com");
        assertThat(principal.getNickname()).isEqualTo("stella");
    }

    @Test
    void 토큰이_없으면_익명_사용자로_통과시킨다()
            throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(chain.getRequest()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();
    }

    @Test
    void refreshToken은_API_인증에_사용할_수_없다()
            throws ServletException, IOException {
        String refreshToken = jwtProvider.createRefreshToken(7L);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(
                HttpHeaders.AUTHORIZATION,
                "Bearer " + refreshToken
        );
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(chain.getRequest()).isNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();
    }
}
