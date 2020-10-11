package com.donghun.logintoken.account;

import com.donghun.logintoken.BaseTest;
import com.donghun.logintoken.account.dto.AccountDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class AccountControllerTest extends BaseTest {

    @DisplayName("POST /sign-up TEST")
    @Test
    public void postSignUp() throws Exception {
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
}
