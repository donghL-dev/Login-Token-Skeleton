package com.donghun.logintoken;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class LoginTokenApplicationTests {

    @Test
    void contextLoads() {
        LoginTokenApplication.main(new String[]{});
    }

}
