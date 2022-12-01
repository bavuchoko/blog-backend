package com.pjs.blog.commons.accounts.web;

import com.pjs.blog.commons.accounts.CurrentUser;
import com.pjs.blog.commons.accounts.dto.AccountDto;
import com.pjs.blog.commons.accounts.dto.LoginResponseBody;
import com.pjs.blog.commons.accounts.entity.Account;
import com.pjs.blog.commons.accounts.entity.AccountRole;
import com.pjs.blog.commons.accounts.service.AccountServiceImpl;
import com.pjs.blog.config.security.jjwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/api/user",  produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class AccountController {

    private final AccountServiceImpl accountService;

    @PostMapping("/authenticate")
    public ResponseEntity authenticate(
            @Valid @RequestBody AccountDto accountDto,
            Errors errors,
            HttpServletResponse response) {

        if(errors.hasErrors()){
            return accountService.badRequest(errors);
        }

        try {
            String accessToken = accountService.authorize(accountDto, response);
            LoginResponseBody loginResponseBody =accountService.getCustomResponseBody(accessToken);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + accessToken);
            return new ResponseEntity(loginResponseBody, httpHeaders, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(EntityModel.of(e.getMessage()));
        }
    }

    @PostMapping("/join")
    public ResponseEntity creatAccount(
            @Valid @RequestBody AccountDto accountDto,
            Errors errors,
            HttpServletResponse response) {

        if(errors.hasErrors()){
            return accountService.badRequest(errors);
        }
        try {
            accountDto.setRoles(Set.of(AccountRole.USER));
            accountService.saveAccount(accountDto);
            String accessToken = accountService.authorize(accountDto, response);
            LoginResponseBody loginResponseBody =accountService.getCustomResponseBody(accessToken);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + accessToken);
            return new ResponseEntity(loginResponseBody, httpHeaders, HttpStatus.OK);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Check Username or password");
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(EntityModel.of(e.getMessage()));
        }

    }


    @GetMapping("/reIssue")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity refreshToken(
            @CookieValue(value = "refreshToken", required=false, defaultValue = "empty") String refreshToken){
        try {
            String accessToken = accountService.reIssue(refreshToken);
            LoginResponseBody loginResponseBody =accountService.getCustomResponseBody(accessToken);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + accessToken);
            return new ResponseEntity(loginResponseBody, httpHeaders, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(EntityModel.of(e.getMessage()));
        }
    }



    @GetMapping("/logout")
    public ResponseEntity logoutUser(
            HttpServletRequest request,
            @CurrentUser Account account) {
            accountService.logoutUser(account, request);
            return ResponseEntity.ok().body("logged out");
    }


    @GetMapping("/usertest")
    @PreAuthorize("hasAnyRole('USER')")
    public String usertest(){
        return "only user permiited";
    }


    @GetMapping("/alluser")
    public ResponseEntity permiAll(
            Pageable pageable,
            PagedResourcesAssembler<Account> assembler
    ){
        Page<Account> page = accountService.getAllUser(pageable);
        var pageResources = assembler.toModel(page, entity -> EntityModel.of(entity).add(linkTo(AccountController.class).withSelfRel()));
        pageResources.add(Link.of("/docs/index/html").withRel("profile"));
        return ResponseEntity.ok().body(pageResources);
    }
}
