package com.donghun.logintoken.account;

import com.donghun.logintoken.account.dto.AccountDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository accountRepository;

    @PostMapping("/sign-up")
    public ResponseEntity<?> postSignUp(@RequestBody @Valid AccountDTO accountDTO, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body("{\"message\" : \"잘못된 요청입니다.\"");
        }
        WebMvcLinkBuilder self = linkTo(AccountController.class).slash("/sign-up");

        Account saveAccount = accountRepository.save(accountDTO.toEntity());
        EntityModel<Account> entityModel = EntityModel.of(saveAccount)
                .add(self.withSelfRel());

        return ResponseEntity.created(self.toUri()).body(entityModel);
    }
}
