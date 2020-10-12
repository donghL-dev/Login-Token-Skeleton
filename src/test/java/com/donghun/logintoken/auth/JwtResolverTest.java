package com.donghun.logintoken.auth;

import com.donghun.logintoken.account.Account;
import com.donghun.logintoken.account.AccountRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class JwtResolverTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtResolver jwtResolver;

    @AfterEach
    public void dbClean() {
        accountRepository.deleteAll();
    }

    @DisplayName("토큰 생성 테스트")
    @Test
    public void createJwtTokenTest() {
        Account account = accountRepository.save(Account.builder()
                .email("test@test.com")
                .password(passwordEncoder.encode("testPassword1234"))
                .build());

        String token = jwtResolver.createJwtToken(account);

        assertThat(token).isNotNull();
    }

    @DisplayName("생성된 토큰 파싱 테스트")
    @Test
    public void getParsedTokenTest() {
        Account account = accountRepository.save(Account.builder()
                .email("test@test.com")
                .password(passwordEncoder.encode("testPassword1234"))
                .build());

        String token = jwtResolver.createJwtToken(account);
        assertThat(token).isNotNull();

        Jws<Claims> parsedToken = jwtResolver.getParsedToken(token);
        assertThat(parsedToken).isNotNull();
    }

    @DisplayName("파싱된 토큰을 이용해서 Account 정보 조회 테스트")
    @Test
    public void getAccountByParsedTokenTest() {
        Account account = accountRepository.save(Account.builder()
                .email("test@test.com")
                .password(passwordEncoder.encode("testPassword1234"))
                .build());

        String token = jwtResolver.createJwtToken(account);
        assertThat(token).isNotNull();

        Jws<Claims> parsedToken = jwtResolver.getParsedToken(token);
        assertThat(parsedToken).isNotNull();

        Account parsedAccountInfo = jwtResolver.getAccountByParsedToken(parsedToken);
        assertThat(parsedAccountInfo).isNotNull();
        assertThat(parsedAccountInfo.getEmail()).isEqualTo(account.getEmail());
        assertThat(parsedAccountInfo.getId()).isEqualTo(account.getId());
    }

    @DisplayName("파싱된 토큰을 이용해서 Authorities 정보 조회 테스트")
    @Test
    public void getAuthoritiesByParsedTokenTest() {
        Account account = accountRepository.save(Account.builder()
                .email("test@test.com")
                .password(passwordEncoder.encode("testPassword1234"))
                .build());

        String token = jwtResolver.createJwtToken(account);
        assertThat(token).isNotNull();

        Jws<Claims> parsedToken = jwtResolver.getParsedToken(token);
        assertThat(parsedToken).isNotNull();

        List<GrantedAuthority> parsedAuthorities = jwtResolver.getAuthoritiesByParsedToken(parsedToken);
        assertThat(parsedAuthorities).isNotEmpty();
        assertThat(parsedAuthorities).isEqualTo(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
