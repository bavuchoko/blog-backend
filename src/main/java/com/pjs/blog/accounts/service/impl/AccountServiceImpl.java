package com.pjs.blog.accounts.service.impl;

import com.pjs.blog.accounts.AccountAdapter;
import com.pjs.blog.accounts.dto.AccountDto;
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
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService, UserDetailsService {

    private final AccountJapRepository accountJapRepository;
    private final CookieUtil cookieUtil;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenManager tokenManager;
    private final RedisUtil redisUtil;
    private final PasswordEncoder passwordEncoder;

    private final String BLOCK_ACCESS_TOKEN_PREFIX = "LOGOUT:";



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountJapRepository.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException(username));
        return new AccountAdapter(account);
    }

    @Override
    public ResponseEntity authorize(
            AccountDto accountDto,
            HttpServletResponse response,
            String message
    ) throws BadCredentialsException {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(accountDto.getUsername(), accountDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenManager.createToken(authentication, TokenType.ACCESS_TOKEN);
        LoginResponseBody loginResponseBody = LoginResponseBody.builder()
                .success("true")
                .token(accessToken)
                .username(authentication.getName())
                .nickname(tokenManager.getNickname(accessToken)==null ? "익명" : tokenManager.getNickname(accessToken))
                .message( message + "에 성공하였습니다.")
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

    @Override
    public Account saveAccount(AccountDto accountDto) {

        Account account = accountDto.toEntity();
        account.joinDateSetter();
        accountJapRepository.findByUsername(account.getUsername()).ifPresent(e->{
            throw new IllegalStateException("이미 가입된 이메일 입니다.");
        });
        account.passwordSetter(this.passwordEncoder.encode(account.getPassword()));
        return accountJapRepository.save(account);
    }

    @Override
    public Page<Account> getAllUser(Pageable pageable) {
        return accountJapRepository.findAll(pageable);
    }

    @Override
    public void logoutUser(Account accout, HttpServletRequest request) {
        try{
            String accessToken = tokenManager.resolveToken(request);
            //토큰만료시간을 가져와서 Date 타입의 만료시간을 long 타입으로 변환해 redis 의 유효기간으로 넣어줌.
            Date expiration = tokenManager.getClaims(accessToken).getExpiration();
            SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy.MM.dd/HH:mm:ss");
            long expiredAt = (df.parse(df.format(expiration))).getTime();
            redisUtil.setDataExpire(BLOCK_ACCESS_TOKEN_PREFIX + accessToken, accout.getUsername(),expiredAt);
            SecurityContextHolder.clearContext();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
