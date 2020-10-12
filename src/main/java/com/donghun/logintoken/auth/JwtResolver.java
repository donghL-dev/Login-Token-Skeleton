package com.donghun.logintoken.auth;

import com.donghun.logintoken.account.Account;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtResolver {

    private final String KEY = "secret";

    public String createJwtToken(Account account) {
        long EXPIRED_AT = 60 * 30 * 1000;
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, KEY)
                .setHeaderParam("typ", "JwtToken")
                .setIssuer("Token Base Login Project")
                .setAudience("Token Base Login Project")
                .setSubject("JWT Token")
                .claim("id", account.getId())
                .claim("email", account.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRED_AT))
                .claim("role", Collections.singletonList("ROLE_USER"))
                .compact();
    }

    public Jws<Claims> getParsedToken(String token) {
        return Jwts.parser().setSigningKey(KEY)
                .parseClaimsJws(token);
    }

    public Account getAccountByParsedToken(Jws<Claims> parsedToken) {
        String email = (String) parsedToken.getBody().get("email");
        Integer userId = (Integer) parsedToken.getBody().get("id");

        return Account.builder().id(userId.longValue()).email(email).build();
    }

    public List<GrantedAuthority> getAuthoritiesByParsedToken(Jws<Claims> parsedToken) {
        return  ((List<?>) parsedToken.getBody()
                .get("role")).stream()
                .map(authority -> new SimpleGrantedAuthority((String) authority))
                .collect(Collectors.toList());
    }
}
