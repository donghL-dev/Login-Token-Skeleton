package com.donghun.logintoken.account.dto;


import com.donghun.logintoken.account.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {

    @Email @NotBlank
    private String email;

    @NotBlank @Length(min = 8, max = 20)
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
