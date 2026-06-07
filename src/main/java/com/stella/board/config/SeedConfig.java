package com.stella.board.config;

import lombok.RequiredArgsConstructor;
import com.stella.board.user.User;
import com.stella.board.user.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

@Configuration
@Profile("development")
@RequiredArgsConstructor
public class SeedConfig {

    private final UserRepository userRepository;

    @Bean
    ApplicationRunner seedRunner() {
        return arguments -> seed(); // 부트 기동 후 1회 실행
    }

    @Transactional
    void seed() {
        if (userRepository.count() >= 10) return;

        // tester1 ~ tester10 계정 더미 데이터
        IntStream.rangeClosed(1, 10).forEach(i -> {
            String password = "12341234aS!" + i;
            User user = new User("tester" + i + "@adapterz.kr", password, "tester" + i);
            userRepository.save(user);
        });
    }
}