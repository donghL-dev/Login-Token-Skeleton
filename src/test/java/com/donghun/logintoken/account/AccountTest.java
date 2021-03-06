package com.donghun.logintoken.account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountTest {

    @DisplayName("Account Builder 생성 테스트")
    @Test
    public void createBuilderAccountTest() {
        String email = "test@test.com";
        String password = "password1234";
        String name = "name";
        String picture = "image Link";

        Account account = Account.builder()
                .email(email)
                .password(password)
                .name(name)
                .picture(picture)
                .build();

        assertThat(account).isNotNull();
        assertThat(account.getEmail()).isEqualTo(email);
        assertThat(account.getPassword()).isEqualTo(password);
        assertThat(account.getName()).isEqualTo(name);
        assertThat(account.getPicture()).isEqualTo(picture);
    }

    @DisplayName("Account Setter 생성 테스트")
    @Test
    public void createSetterAccountTest() {
        String email = "test@test.com";
        String password = "password1234";
        String name = "name";
        String picture = "image Link";

        Account account = new Account();
        account.setEmail(email);
        account.setPassword(password);
        account.setName(name);
        account.setPicture(picture);

        assertThat(account).isNotNull();
        assertThat(account.getEmail()).isEqualTo(email);
        assertThat(account.getPassword()).isEqualTo(password);
        assertThat(account.getName()).isEqualTo(name);
        assertThat(account.getPicture()).isEqualTo(picture);
    }

}
