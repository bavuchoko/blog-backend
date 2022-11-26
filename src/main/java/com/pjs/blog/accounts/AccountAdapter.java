package com.pjs.blog.accounts;

import com.pjs.blog.accounts.entity.Account;
import com.pjs.blog.accounts.entity.AccountRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class AccountAdapter extends User {
    private Account account;

    public AccountAdapter(Account account) {
        super(account.getUsername(), account.getPassword(), authrities(account.getRoles()));
    }

    private static Collection<? extends GrantedAuthority> authrities(Set<AccountRole> roles) {
        return roles.stream().map(r->new SimpleGrantedAuthority("ROLE_" + r.name()))
                .collect(Collectors.toSet());
    }

    public Account getAccount() {
        return account;
    }
}
