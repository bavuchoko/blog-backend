package com.pjs.blog.config.security.jjwt;

public enum TokenType {
    ACCESS_TOKEN("accessToken"), REFRESH_TOKEN("refreshToken");

    private final String value;

    TokenType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
