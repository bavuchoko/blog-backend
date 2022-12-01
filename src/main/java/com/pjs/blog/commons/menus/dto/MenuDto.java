package com.pjs.blog.commons.menus.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.pjs.blog.commons.accounts.entity.Account;
import com.pjs.blog.commons.menus.entity.Menu;
import lombok.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuDto {
    private static ModelMapper modelMapper = new ModelMapper();

    private Integer id;
    private int parentKey;

    @NotBlank(message = "메뉴명은 필수값 입니다.")
    private String menuName;

    private String path;
    private String description;
    private LocalDateTime createDate;

    public Menu toEntity() {
        modelMapper.getConfiguration()
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
                .setFieldMatchingEnabled(true);
        return modelMapper.map(this, Menu.class);
    }
}
