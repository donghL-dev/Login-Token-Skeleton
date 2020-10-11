package com.donghun.logintoken.account;

import com.donghun.logintoken.BaseTest;
import com.donghun.logintoken.account.dto.AccountDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class AccountControllerTest extends BaseTest {

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter(StandardCharsets.UTF_8.name(), true))
                .build();
    }

    @AfterEach
    public void dbClean() {
        accountRepository.deleteAll();
    }

    @DisplayName("POST /sign-up Success TEST")
    @Test
    public void postSignUpSuccessTest() throws Exception {
        String email = "test@test.com" ;
        String password = "testpassword1234";
        String name = "name";
        String picture = "image.link";

        AccountDTO accountDTO = AccountDTO.builder()
                .email(email)
                .password(password)
                .name(name)
                .picture(picture)
                .build();

        mockMvc.perform(post("/api/accounts/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDTO)))
                .andDo(print())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("picture").exists())
                .andExpect(jsonPath("_links").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isCreated());
    }

    @DisplayName("POST /sign-up Fail TEST")
    @ParameterizedTest(name = "#{index} : {2}")
    @MethodSource("params")
    public void postSignUpFailTest(String email, String password, String message) throws Exception {
        String name = "name";
        String picture = "image.link";

        AccountDTO accountDTO = AccountDTO.builder()
                .email(email)
                .password(password)
                .name(name)
                .picture(picture)
                .build();

        mockMvc.perform(post("/api/accounts/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDTO)))
                .andDo(print())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("message").value("잘못된 요청입니다."))
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
                Arguments.of("email", "11", "이메일, 패스워드 형식 오류")
        );
    }

    @DisplayName("Account 조회 성공 테스트")
    @Test
    public void getAccountSuccessTest() throws Exception {
        AccountDTO accountDTO = AccountDTO.builder()
                .email("test@test.com")
                .password("password")
                .name("name")
                .picture("picture")
                .build();

        Account account = accountRepository.save(accountDTO.toEntity());

        mockMvc.perform(get("/api/accounts/" + account.getId()))
                .andDo(print())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("id").value(account.getId()))
                .andExpect(jsonPath("email").exists())
                .andExpect(jsonPath("email").value(accountDTO.getEmail()))
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("name").value(accountDTO.getName()))
                .andExpect(jsonPath("picture").exists())
                .andExpect(jsonPath("picture").value(accountDTO.getPicture()))
                .andExpect(jsonPath("_links").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

    }

    @DisplayName("Account 조회 실패 테스트")
    @Test
    public void getAccountFailTest() throws Exception {
        Random random = new Random();
        mockMvc.perform(get("/api/accounts/" + random.nextInt(Integer.MAX_VALUE) ))
                .andDo(print())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("message").value("잘못된 요청입니다."))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

}
