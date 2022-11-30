package com.pjs.blog.config.security.jjwt;

import com.pjs.blog.config.redis.RedisUtil;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final TokenManager tokenManager;
    private final RedisUtil redisUtil;
    public JwtSecurityConfig(TokenManager tokenProvider, RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
        this.tokenManager = tokenProvider;
    }



    @Override
    public void configure(HttpSecurity http) {
        JwtFilter customFilter = new JwtFilter(tokenManager, redisUtil);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}