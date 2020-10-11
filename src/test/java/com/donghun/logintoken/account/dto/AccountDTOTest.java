package com.donghun.logintoken.account.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountDTOTest {

    @DisplayName("AccountDTO 생성 테스트")
    @Test
    public void createAccountDTOTest() {
        String email = "test@test.com";
        String password = "password1234";
        String name = "name";
        String picture = "image Link";

        AccountDTO accountDTO = AccountDTO.builder()
                .email(email)
                .password(password)
                .name(name)
                .picture(picture)
                .build();

        assertThat(accountDTO).isNotNull();
        assertThat(accountDTO.getEmail()).isEqualTo(email);
        assertThat(accountDTO.getPassword()).isEqualTo(password);
        assertThat(accountDTO.getName()).isEqualTo(name);
        assertThat(accountDTO.getPicture()).isEqualTo(picture);
    }
}
