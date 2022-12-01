package com.pjs.blog.commons.menus.repository;

import com.pjs.blog.commons.menus.entity.Menu;

import com.pjs.blog.commons.menus.entity.QMenu;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MenuRepositoryImpl implements MenuRepository{
    private final JPAQueryFactory jpaQueryFactory;



    @Override
    public List<Menu> findAll() {
        QMenu qMenu = QMenu.menu;
        return jpaQueryFactory.selectFrom(qMenu).fetch();
    }
}
