package com.stella.board.user.auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Configuration
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret ;
    private long accessTokenExpSeconds;
    private long refreshTokenExpSeconds;
}