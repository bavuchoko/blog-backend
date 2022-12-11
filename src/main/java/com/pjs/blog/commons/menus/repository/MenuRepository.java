package com.pjs.blog.commons.menus.repository;

import com.pjs.blog.commons.accounts.entity.Account;
import com.pjs.blog.commons.accounts.entity.AccountRole;
import com.pjs.blog.commons.menus.entity.Menu;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.pjs.blog.commons.menus.entity.QMenu.menu;

@Repository
@RequiredArgsConstructor
public class MenuRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public List<Menu> findAll() {
        return jpaQueryFactory.selectFrom(menu).fetch();
    }

    public List<Menu> findAllByUser(List<String> roles) {
        return jpaQueryFactory.selectFrom(menu).where((menu.role.in(roles))).fetch();
    }


}
