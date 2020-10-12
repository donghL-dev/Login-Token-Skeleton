package com.donghun.logintoken.account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AccountRepositoryTest {

    @Autowired
    AccountRepository accountRepository;

    @Test
    @DisplayName("Account 등록 테스트")
    void test_db() {
        Account account = Account.builder()
                .email("test@test.com")
                .password("password")
                .build();

        accountRepository.save(account);

        Account dbAccount = accountRepository.findByEmail(account.getEmail());

        assertThat(dbAccount).isNotNull();
        assertThat(dbAccount.getId()).isNotNull();
        assertThat(account.getEmail()).isEqualTo(dbAccount.getEmail());
        assertThat(account.getPassword()).isEqualTo(dbAccount.getPassword());
    }
}
