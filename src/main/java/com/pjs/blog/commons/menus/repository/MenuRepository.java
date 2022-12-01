package com.pjs.blog.commons.menus.repository;

import com.pjs.blog.commons.menus.entity.Menu;

import java.util.List;

public interface MenuRepository {

    List<Menu> findAll();
}
