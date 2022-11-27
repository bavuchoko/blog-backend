package com.pjs.blog.accounts.web;

import com.pjs.blog.accounts.dto.AccountDto;
import com.pjs.blog.accounts.dto.LoginResponseBody;
import com.pjs.blog.accounts.entity.Account;
import com.pjs.blog.accounts.entity.AccountRole;
import com.pjs.blog.accounts.service.impl.AccountServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Set;

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
            return accountService.authorize(accountDto, response, "로그인");
        } catch (BadCredentialsException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(EntityModel.of(new LoginResponseBody(e.getMessage())));
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
            return accountService.authorize(accountDto, response, "회원가입");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(EntityModel.of(new LoginResponseBody(e.getMessage())));
        }

    }

    @GetMapping("/usertest")
    @PreAuthorize("hasAnyRole('USER')")
    public String usertest(){
        return "only user permiited";
    }


    @GetMapping("/alluser")
    public String permiAll(){
        return "all requests are permiited";
    }
}
