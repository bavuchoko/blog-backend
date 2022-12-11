package com.pjs.blog.commons.menus.service;

import com.pjs.blog.commons.menus.entity.Menu;

import java.util.List;

public interface MenuService {


    List<Menu> findAllByUser(List<String> roles);

}
