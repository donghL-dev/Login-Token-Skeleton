package com.donghun.logintoken.auth.filter;

import com.donghun.logintoken.account.Account;
import com.donghun.logintoken.auth.JwtResolver;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

    private JwtResolver jwtResolver;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    public void setJwtResolver(ApplicationContext context) {
        this.jwtResolver = context.getBean(JwtResolver.class);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        if (authentication == null) {
            chain.doFilter(request, response);
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader("AUTH-TOKEN");

        if (token != null && !token.isEmpty()) {
            try {
                Jws<Claims> parsedToken = jwtResolver.getParsedToken(token);
                Account account = jwtResolver.getAccountByParsedToken(parsedToken);
                List<GrantedAuthority> authorities = jwtResolver.getAuthoritiesByParsedToken(parsedToken);

                if (account != null && !authorities.isEmpty()) {
                    return new UsernamePasswordAuthenticationToken(account, null, authorities);
                }
            } catch (ExpiredJwtException exception) {
                log.warn("Request to parse expired JwtToken : {} failed : {}", token, exception.getMessage());
            } catch (UnsupportedJwtException exception) {
                log.warn("Request to parse unsupported JwtToken : {} failed : {}", token, exception.getMessage());
            } catch (MalformedJwtException exception) {
                log.warn("Request to parse invalid JwtToken : {} failed : {}", token, exception.getMessage());
            } catch (SignatureException exception) {
                log.warn("Request to parse JwtToken with invalid signature : {} failed : {}", token, exception.getMessage());
            } catch (IllegalArgumentException exception) {
                log.warn("Request to parse empty or null JwtToken : {} failed : {}", token, exception.getMessage());
            }
        }

        return null;
    }
}
