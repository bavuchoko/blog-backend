package com.pjs.blog.commons.accounts.dto;

import com.pjs.blog.commons.accounts.entity.Account;
import com.pjs.blog.commons.accounts.entity.AccountRole;
import lombok.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private static ModelMapper modelMapper = new ModelMapper();

    private Integer id;

    @NotBlank(message = "아이디는 필수값 입니다.")
    @Email(message = "아이디는 이메일 형식이어야 합니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수값 입니다.")
    private String password;

    private String nickname;
    private Set<AccountRole> roles;

    private LocalDateTime joinDate;


    public Account toEntity() {
        modelMapper.getConfiguration()
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
                .setFieldMatchingEnabled(true);
        return modelMapper.map(this, Account.class);
    }

}
