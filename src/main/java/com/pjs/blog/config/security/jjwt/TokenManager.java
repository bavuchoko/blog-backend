package com.pjs.blog.config.security.jjwt;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public interface TokenManager {
    String createToken(Authentication authentication, TokenType tokenType);
    boolean validateToken(String token);
    boolean validateRefreshToken(String refreshToken);
    void destroyRefreshTokens(HttpServletRequest request);
    Authentication getAuthentication(String token);

    Claims getClaims(String accessToken);

    String resolveToken(HttpServletRequest req);

    String getNickname(String accessToken);

    String getUsername(String accessToken);
}
