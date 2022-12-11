package com.pjs.blog.commons.menus.dto;


import com.pjs.blog.commons.accounts.entity.AccountRole;
import com.pjs.blog.commons.menus.entity.Menu;
import lombok.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuDto {
    private static ModelMapper modelMapper = new ModelMapper();

    private Integer id;
    private int orders;

    @NotBlank(message = "메뉴명은 필수값 입니다.")
    private String menuName;

    private String description;
    private LocalDateTime createDate;

    private Menu parent;
    private List<Menu> child;

    private String role;

    public Menu toEntity() {
        modelMapper.getConfiguration()
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
                .setFieldMatchingEnabled(true);
        return modelMapper.map(this, Menu.class);
    }
}
