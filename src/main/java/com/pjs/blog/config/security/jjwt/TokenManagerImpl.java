package com.pjs.blog.config.security.jjwt;

import com.pjs.blog.accounts.AccountAdapter;
import com.pjs.blog.accounts.entity.Account;
import com.pjs.blog.accounts.repository.AccountJapRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;


@Component
public class TokenManagerImpl implements TokenManager, InitializingBean {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final Logger logger = LoggerFactory.getLogger(TokenManagerImpl.class);

    private static final String AUTHORITIES_KEY = "auth";

    private final String secret;
    private final long accessTokenValidityTime;
    private final long refreshTokenValidityTime;

    private Key key;
    @Autowired
    AccountJapRepository accountJapRepository;

    public TokenManagerImpl(
            @Value("${spring.jwt.secret}") String secret,
            @Value("${spring.jwt.token-validity-in-millisecond}") long oneDayInMilliseconds ){
        this.secret = secret;
//        24시간
        this.accessTokenValidityTime = oneDayInMilliseconds;
//        1주일
        this.refreshTokenValidityTime = oneDayInMilliseconds * 7 ;
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

        AccountAdapter account25 = (AccountAdapter)(authentication.getPrincipal());
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
    public boolean validateToken(String token) {
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
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        String username = claims.get("username", String.class);
        UserDetails aa=  new AccountAdapter(accountJapRepository.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException(username)));

        return new UsernamePasswordAuthenticationToken(aa, token, authorities);
    }

    @Override
    public Claims getClaims(String accessToken) {
      return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
    }



    public String getUsername(String token) {
        return getClaims(token).get("username").toString();
    }

    public String getNickname(String token) {
        return getClaims(token).get("nickname").toString();
    }

    @Override
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }


}
