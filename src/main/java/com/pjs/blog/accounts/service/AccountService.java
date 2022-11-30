package com.pjs.blog.accounts.service;

import com.pjs.blog.accounts.dto.AccountDto;
import com.pjs.blog.accounts.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AccountService {


    ResponseEntity authorize(AccountDto accountDto, HttpServletResponse response, String message);
    Account saveAccount(AccountDto accountDto);
    Page<Account> getAllUser(Pageable pageable);
    void logoutUser(Account account, HttpServletRequest req);
}
