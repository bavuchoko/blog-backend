package com.pjs.blog.accounts.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseBody {
    private String success;
    private String token;
    private String username;
    private String nickname;
    private String message;


    public LoginResponseBody(String message) {
        this.success = "fail";
        this.message = message;
    }
}
