package com.pjs.blog.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {


//    Authentication auth
//            = SecurityContextHolder.getContext().getAuthentication();
//        if (auth != null) {
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        response.setStatus(HttpStatus.FORBIDDEN.value());
//        Map<String,Object> errMsg = new HashMap<>();
//        errMsg.put("status",403);
//        errMsg.put("code",HttpStatus.FORBIDDEN);
//        errMsg.put("message","접근 권한이 없습니다.");
//        try (OutputStream os = response.getOutputStream()) {
//            ObjectMapper objectMapper = new ObjectMapper();
//            objectMapper.writeValue(os, errMsg);
//            os.flush();
//        }
//    }


    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.sendError(HttpServletResponse.SC_FORBIDDEN); // 403에러
    }
}
