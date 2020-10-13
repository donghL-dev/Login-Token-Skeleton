package com.donghun.logintoken;

import com.donghun.logintoken.account.AccountRepository;
import com.donghun.logintoken.account.dto.AccountDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements ApplicationRunner {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        accountRepository.save(AccountDTO.builder()
//                .email("test@test.com").password("password1!").build().toEntity(passwordEncoder));
    }
}
