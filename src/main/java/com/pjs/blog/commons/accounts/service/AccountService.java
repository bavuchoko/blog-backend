package com.pjs.blog.commons.accounts.service;

import com.pjs.blog.commons.accounts.dto.AccountDto;
import com.pjs.blog.commons.accounts.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AccountService {


    String authorize(AccountDto accountDto, HttpServletResponse response);
    Account saveAccount(AccountDto accountDto);
    Page<Account> getAllUser(Pageable pageable);
    void logoutUser(Account account, HttpServletRequest req);
    String reIssue(String refreshToken);
}
