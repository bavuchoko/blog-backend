package com.pjs.blog.commons.menus.service;

import com.pjs.blog.commons.menus.entity.Menu;
import com.pjs.blog.commons.menus.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService{

    private final MenuRepository menuRepository;

    @Override
    public List<Menu> findAllByUser(List<String> roles) {

        return  menuRepository.findAllByUser(roles);
    }

}
