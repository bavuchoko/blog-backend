package com.pjs.blog.commons.menus.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Menu {
    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    @Column(name = "menu_id")
    private Integer id;

    private int parentKey;

    @Column(unique = true)
    private String menuName;
    @Transient
    private String path;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;


}
