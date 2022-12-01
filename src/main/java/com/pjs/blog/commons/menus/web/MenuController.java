package com.pjs.blog.commons.menus.web;


import com.pjs.blog.commons.menus.entity.Menu;
import com.pjs.blog.commons.menus.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/menu",  produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class MenuController {

    private final MenuRepository menuRepository;

    @GetMapping
    public void getMenuList() {
        List<Menu> menus = menuRepository.findAll();

    }

}
