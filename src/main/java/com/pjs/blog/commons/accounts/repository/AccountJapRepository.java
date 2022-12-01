package com.pjs.blog.commons.accounts.repository;


import com.pjs.blog.commons.accounts.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountJapRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByUsername(String username);
}
