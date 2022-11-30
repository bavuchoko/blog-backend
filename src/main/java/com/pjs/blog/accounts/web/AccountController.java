package com.pjs.blog.accounts.web;

import com.pjs.blog.accounts.CurrentUser;
import com.pjs.blog.accounts.dto.AccountDto;
import com.pjs.blog.accounts.dto.LoginResponseBody;
import com.pjs.blog.accounts.entity.Account;
import com.pjs.blog.accounts.entity.AccountRole;
import com.pjs.blog.accounts.service.impl.AccountServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
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

    @PostMapping("/logout")
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
