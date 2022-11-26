package com.pjs.blog.config.security.jjwt;

import com.pjs.blog.accounts.AccountAdapter;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class TokenManagerImpl implements TokenManager, InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(TokenManagerImpl.class);

    private static final String AUTHORITIES_KEY = "auth";

    private final String secret;
    private final long accessTokenValidityTime;
    private final long refreshTokenValidityTime;

    private Key key;

    public TokenManagerImpl(
            @Value("{spring.jwt.secret}") String secret,
            @Value("{spring.jwt.token-validity-in-millisecond}") long tokenValidityInSeconds ){
        this.secret = secret;
//        24시간
        this.accessTokenValidityTime = tokenValidityInSeconds;
//        1주일
        this.refreshTokenValidityTime = tokenValidityInSeconds * 7 ;
    }

    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String createToken(Authentication authentication, TokenType tokenType) {

    //토큰에 담을 정보들
        //시큐리티 로그인 정보로 부터가저오는 정보
            //권한
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
            //닉네임
        String nickName = ((AccountAdapter)(authentication.getPrincipal())).getAccount().getNickname();

        //접속가능시간, 갱신가능시간세팅
        long now =  (new Date()).getTime();
        Date validity = tokenType == TokenType.ACCESS_TOKEN ?
                new Date(now + this.accessTokenValidityTime)
                : new Date(now + this.refreshTokenValidityTime);

        Map<String, Object> payLoad = new HashMap<>();

        payLoad.put("username", authentication.getName());
        payLoad.put(AUTHORITIES_KEY, authorities);
        payLoad.put("nickname", nickName==null ? "ANONYMOUS" : nickName);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .setClaims(payLoad)
                .signWith(key, SignatureAlgorithm.HS512)
                .setIssuedAt(new Date())
                .setExpiration(validity)
                .compact()
                ;
    }

    @Override
    public String refreshAccessToken(HttpServletRequest request) {


        return null;
    }

    @Override
    public boolean validateToken(String token, HttpServletRequest request) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            logger.info("잘못된 JWT 서명입니다.");
            throw e;
        } catch (ExpiredJwtException e) {
            logger.info("만료된 JWT 토큰입니다.");
            throw e;
        } catch (UnsupportedJwtException e) {
            logger.info("지원되지 않는 JWT 토큰입니다.");
            throw e;
        } catch (IllegalArgumentException e) {
            logger.info("JWT 토큰이 잘못되었습니다.");
            throw e;
        }
    }

    @Override
    public boolean validateRefreshToken(HttpServletRequest request) {

        return false;
    }

    @Override
    public void destroyTokens(HttpServletRequest request) {

    }

    @Override
    public Authentication getAuthentication(String token) {
        return null;
    }

    @Override
    public String getUsername(String accessToken) {
        return null;
    }

    @Override
    public String getNickname(String accessToken) {
        return null;
    }
}
