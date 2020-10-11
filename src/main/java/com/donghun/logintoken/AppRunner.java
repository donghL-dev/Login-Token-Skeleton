package com.donghun.logintoken;

import com.donghun.logintoken.account.Account;
import com.donghun.logintoken.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AppRunner implements CommandLineRunner {

    private final AccountRepository accountRepository;

    @Override
    public void run(String... args) throws Exception {
//        Account account = Account.builder()
//                .email("AppRunner@email.com")
//                .password("AppRunnerPassword")
//                .build();
//
//        accountRepository.save(account);
    }
}
