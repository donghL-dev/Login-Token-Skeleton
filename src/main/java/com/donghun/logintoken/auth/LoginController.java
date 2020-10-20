package com.donghun.logintoken.auth;

import com.donghun.logintoken.account.Account;
import com.donghun.logintoken.account.AccountRepository;
import com.donghun.logintoken.account.dto.AccountDTO;
import com.donghun.logintoken.account.dto.GoogleOAuthRequest;
import com.donghun.logintoken.account.dto.GoogleOAuthResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class LoginController {

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    private final Environment env;

    private final JwtResolver jwtResolver;

    private static final String CLIENT_PROPERTY_KEY = "spring.security.oauth2.client.registration.";

    private static final String GOOGLE_TOKEN_BASE_URL = "https://oauth2.googleapis.com/token";

    @PostMapping("/api/login")
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

    @GetMapping(value = "/api/login/oauth2-google", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getGoogleLoginCode(@RequestParam(value = "code") String authCode) throws Exception {
        String clientId = env.getProperty(CLIENT_PROPERTY_KEY + "google" + ".client-id");
        String clientSecret = env.getProperty(CLIENT_PROPERTY_KEY + "google" + ".client-secret");

        //HTTP Request를 위한 RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        //Google OAuth Access Token 요청을 위한 파라미터 세팅
        GoogleOAuthRequest googleOAuthRequestParam = GoogleOAuthRequest
                .builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .code(authCode)
                .redirectUri("http://localhost:8080/api/login/oauth2-google")
                .grantType("authorization_code").build();

        //JSON 파싱을 위한 기본값 세팅
        //요청시 파라미터는 스네이크 케이스로 세팅되므로 Object mapper에 미리 설정해준다.
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        //AccessToken 발급 요청
        ResponseEntity<String> resultEntity = restTemplate.postForEntity(GOOGLE_TOKEN_BASE_URL, googleOAuthRequestParam, String.class);

        //Token Request
        GoogleOAuthResponse result = mapper.readValue(resultEntity.getBody(), new TypeReference<GoogleOAuthResponse>() {});

        System.out.println(resultEntity.getBody());

        //ID Token만 추출 (사용자의 정보는 jwt로 인코딩 되어있다)
        String jwtToken = result.getIdToken();
        String requestUrl = UriComponentsBuilder.fromHttpUrl("https://oauth2.googleapis.com/tokeninfo")
                .queryParam("id_token", jwtToken).encode().toUriString();

        String resultJson = restTemplate.getForObject(requestUrl, String.class);
        Map<String,String> userInfo = mapper.readValue(resultJson, new TypeReference<Map<String, String>>(){});

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .body("{\"token\" : \"" + createJwtToken(userInfo) + "\"}");
    }

    private String createJwtToken(Map<String, String> result) {
        String email = result.get("email");
        Account account = accountRepository.findByEmail(email);

        if (account == null) {
            String picture = result.get("picture");
            String name = result.get("name");
            account = accountRepository.save(Account.builder().email(email).name(name).picture(picture).build());
        }

        return jwtResolver.createJwtToken(account);
    }
}
