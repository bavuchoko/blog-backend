package com.pjs.blog.accounts.service.impl;

import com.pjs.blog.accounts.AccountAdapter;
import com.pjs.blog.accounts.dto.LoginResponseBody;
import com.pjs.blog.accounts.entity.Account;
import com.pjs.blog.accounts.repository.AccountJapRepository;
import com.pjs.blog.accounts.service.AccountService;
import com.pjs.blog.accounts.web.AccountController;
import com.pjs.blog.config.cookie.CookieUtil;
import com.pjs.blog.config.redis.RedisUtil;
import com.pjs.blog.config.security.jjwt.JwtFilter;
import com.pjs.blog.config.security.jjwt.TokenManager;
import com.pjs.blog.config.security.jjwt.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService, UserDetailsService {

    private final AccountJapRepository accountJapRepository;
    private final CookieUtil cookieUtil;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenManager tokenManager;
    private final RedisUtil redisUtil;

    @Value("${spring.jwt.token-validity-in-seconds}")
    private long onDayAsMilliseconds;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountJapRepository.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException(username));
        return new AccountAdapter(account);
    }

    public ResponseEntity authrize(
            String username,
            String pass,
            HttpServletResponse response) throws BadCredentialsException {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, pass);

        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenManager.createToken(authentication, TokenType.ACCESS_TOKEN);
        LoginResponseBody loginResponseBody = LoginResponseBody.builder()
                .success("true")
                .token(accessToken)
                .username(authentication.getName())
                .nickname(tokenManager.getNickname(accessToken)==null ? "익명" : tokenManager.getNickname(accessToken))
                .message("로그인에 성공하였습니다.")
                .build();
        String refreshToken = tokenManager.createToken(authentication, TokenType.REFRESH_TOKEN);
        redisUtil.setData(authentication.getName(), refreshToken);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + accessToken);

        Cookie refreshTokenCookie = cookieUtil.createCookie("refreshToken", refreshToken);
        response.addCookie(refreshTokenCookie);

        return new ResponseEntity(loginResponseBody, httpHeaders, HttpStatus.OK);
    }

    public ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(EntityModel.of(errors).add(linkTo(AccountController.class).slash("/join").withRel("redirect")));
    }
}
