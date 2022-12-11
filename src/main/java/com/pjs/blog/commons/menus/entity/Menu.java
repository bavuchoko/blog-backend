package com.pjs.blog.commons.menus.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.pjs.blog.commons.accounts.entity.AccountRole;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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

    private int orders;

    @Column(unique = true)
    private String menuName;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent_id")
    private Menu parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Menu> child;

    private String role;


}
