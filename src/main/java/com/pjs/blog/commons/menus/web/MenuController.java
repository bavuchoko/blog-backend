package com.pjs.blog.commons.menus.web;


import com.pjs.blog.commons.accounts.CurrentUser;
import com.pjs.blog.commons.accounts.entity.Account;
import com.pjs.blog.commons.accounts.entity.AccountRole;
import com.pjs.blog.commons.menus.entity.Menu;
import com.pjs.blog.commons.menus.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.valves.rewrite.InternalRewriteMap;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/api/menus",  produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    public ResponseEntity getMenuList(@CurrentUser Account account) {

        List<String> roles = Arrays.asList(new String[]{AccountRole.PUBLIC.name()});
        if(account !=null)
            roles = account.getRoles().stream().map(e -> e.name()).collect(Collectors.toList());
        List<Menu> menus = menuService.findAllByUser(roles);
        CollectionModel resources = CollectionModel.of(menus);
        return ResponseEntity.ok().body(resources);

    }

}
