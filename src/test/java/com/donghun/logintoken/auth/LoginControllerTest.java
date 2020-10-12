package com.donghun.logintoken.auth;

import com.donghun.logintoken.BaseTest;
import com.donghun.logintoken.account.Account;
import com.donghun.logintoken.account.dto.AccountDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LoginControllerTest extends BaseTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    public void dbClean() {
        accountRepository.deleteAll();
    }

    @DisplayName("Login 성공 테스트")
    @Test
    public void postLoginSuccessTest() throws Exception {
        AccountDTO accountDTO = AccountDTO.builder()
                .email("test@test.com")
                .password("password1234")
                .name("name")
                .picture("picture")
                .build();

        Account account = accountRepository.save(accountDTO.toEntity(passwordEncoder));

        assertThat(account).isNotNull();
        assertThat(account.getId()).isNotNull();

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDTO)))
                .andDo(print())
                .andExpect(jsonPath("token").exists())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @DisplayName("Login 실패 테스트")
    @ParameterizedTest(name = "#{index} : {2}")
    @MethodSource("params")
    public void postLoginFailTest(String email, String password, String message) throws Exception {
        AccountDTO accountDTO = AccountDTO.builder()
                .email(email)
                .password(password)
                .build();

        Account account = accountRepository.save(Account.builder()
                .email("test@test.com")
                .password(passwordEncoder.encode("testPassword1234"))
                .build());

        assertThat(account).isNotNull();
        assertThat(account.getId()).isNotNull();

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDTO)))
                .andDo(print())
                .andExpect(jsonPath("message").exists())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> params() {
        return Stream.of(
                Arguments.of(null, "password", "이메일 입력 X"),
                Arguments.of("email@email.com", null, "패스워드 입력 X"),
                Arguments.of(null, null, "이메일 입력 X, 패스워드 입력 X"),
                Arguments.of("email", "password", "이메일 형식 오류"),
                Arguments.of("email@email.com", "11", "패스워드 8자 미만"),
                Arguments.of("email@email.com", "111111111111111111111111111", "패스워드 20자 초과"),
                Arguments.of("email", "11", "이메일, 패스워드 형식 오류"),
                Arguments.of("email@test.com", "password1234!", "존재하지 않는 아이디"),
                Arguments.of("test@test.com", "testPassword123", "비밀번호 불일치")
        );
    }
}
