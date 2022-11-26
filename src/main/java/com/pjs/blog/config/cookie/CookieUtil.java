package com.pjs.blog.config.cookie;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Service
public class CookieUtil {

    @Value("${spring.jwt.token-validity-in-millisecond}")
    private long oneDayAsMilliseconds;

    long sevenDays = (oneDayAsMilliseconds/1000)*7;

    public Cookie createCookie(String cookieName, String value){
        Cookie token = new Cookie(cookieName,value);
        token.setHttpOnly(true);
        token.setSecure(true);
        token.setMaxAge((int)sevenDays);
        token.setPath("/");
        return token;
    }

    public Cookie getCookie(HttpServletRequest req, String cookieName){
        final Cookie[] cookies = req.getCookies();
        if(cookies==null) return null;
        for(Cookie cookie : cookies){
            if(cookie.getName().equals(cookieName))
                return cookie;
        }
        return null;
    }

}