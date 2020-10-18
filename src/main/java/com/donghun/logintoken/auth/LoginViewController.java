package com.donghun.logintoken.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginViewController {

    @GetMapping("/google-sign-in")
    public String signIn() {
        return "s.html";
    }
}
