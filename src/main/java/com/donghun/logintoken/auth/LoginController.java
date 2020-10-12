package com.donghun.logintoken.auth;

import com.donghun.logintoken.account.Account;
import com.donghun.logintoken.account.AccountRepository;
import com.donghun.logintoken.account.dto.AccountDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/login")
public class LoginController {

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtResolver jwtResolver;

    @PostMapping
    public ResponseEntity<?> postLogin(@RequestBody @Valid AccountDTO accountDTO, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\" : \"잘못된 요청입니다.\"}");
        }

        Account dbAccount = accountRepository.findByEmail(accountDTO.getEmail());

        if (dbAccount == null) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\" : \"존재하지 않는 유저입니다.\"}");
        } else if (!passwordEncoder.matches(accountDTO.getPassword(), dbAccount.getPassword())) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\" : \"비밀번호가 일치하지 않습니다.\"}");
        }

        String token = jwtResolver.createJwtToken(dbAccount);

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .body("{\"token\" : \"" + token + "\"}");
    }
}
