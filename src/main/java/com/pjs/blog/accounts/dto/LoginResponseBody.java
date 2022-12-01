package com.pjs.blog.accounts.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseBody {
    private String success ="fail";
    private String token;
    private String username;
    private String nickname;
    private String message;


}
