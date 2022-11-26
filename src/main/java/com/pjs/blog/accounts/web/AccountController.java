package com.pjs.blog.accounts.web;

import com.pjs.blog.accounts.dto.AccountDto;
import com.pjs.blog.accounts.dto.LoginResponseBody;
import com.pjs.blog.accounts.service.impl.AccountServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/user",  produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class AccountController {

    private final AccountServiceImpl accountService;

    @PostMapping("/authenticate")
    public ResponseEntity authorize(@Valid @RequestBody AccountDto accountDto, Errors errors, HttpServletResponse response) {

        if(errors.hasErrors()){
            return accountService.badRequest(errors);
        }

        try {
            return accountService.authrize(accountDto.getUsername(), accountDto.getPassword(), response);
        } catch (BadCredentialsException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(EntityModel.of(new LoginResponseBody("fail", null, null, null, "아이디와 비밀번호를 확인하세요")));
        }
    }
}
