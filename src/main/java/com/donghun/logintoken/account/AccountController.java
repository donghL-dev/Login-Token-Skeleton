package com.donghun.logintoken.account;

import com.donghun.logintoken.account.dto.AccountDTO;
import com.donghun.logintoken.auth.JwtResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    @PostMapping("/sign-up")
    public ResponseEntity<?> postSignUp(@RequestBody @Valid AccountDTO accountDTO, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\" : \"잘못된 요청입니다.\"}");
        }
        WebMvcLinkBuilder self = linkTo(AccountController.class).slash("/sign-up");

        Account saveAccount = accountRepository.save(accountDTO.toEntity(passwordEncoder));
        EntityModel<Account> entityModel = EntityModel.of(saveAccount)
                .add(self.withSelfRel());

        return ResponseEntity.created(self.toUri()).body(entityModel);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAccount(@PathVariable Long id) {
        Account dbAccount = accountRepository.findById(id).orElse(null);

        if (dbAccount == null) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\" : \"잘못된 요청입니다.\"}");
        }

        WebMvcLinkBuilder self = linkTo(AccountController.class).slash("/" + id);
        EntityModel<Account> entityModel = EntityModel.of(dbAccount)
                .add(self.withSelfRel());

        return ResponseEntity.ok(entityModel);
    }

    @GetMapping(value = "/self-info", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSelfAccount(@AuthAccount Account account) {
        Account dbAccount = accountRepository.findById(account.getId()).orElse(null);

        if (dbAccount == null) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\" : \"잘못된 접근입니다.\"}");
        }

        WebMvcLinkBuilder self = linkTo(AccountController.class).slash("/self-info");
        EntityModel<Account> entityModel = EntityModel.of(dbAccount)
                .add(self.withSelfRel());

        return ResponseEntity.ok(entityModel);
    }
}
