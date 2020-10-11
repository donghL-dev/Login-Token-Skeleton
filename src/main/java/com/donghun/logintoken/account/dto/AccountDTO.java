package com.donghun.logintoken.account.dto;


import com.donghun.logintoken.account.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {

    @Email @NotBlank
    private String email;

    @NotBlank
    private String password;

    private String name;

    private String picture;

    public Account toEntity() {
        return Account.builder()
                .email(this.email)
                .password(this.password)
                .name(this.name)
                .picture(this.picture)
                .build();
    }
}
