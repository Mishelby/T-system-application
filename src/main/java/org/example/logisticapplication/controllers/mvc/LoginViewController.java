package org.example.logisticapplication.controllers.mvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/driver")
@RequiredArgsConstructor
public class LoginViewController {
    private static final String LOGIN_PAGE = "login-page";

    @GetMapping("/login")
    public String showLoginPage() {
        return LOGIN_PAGE;
    }

    @GetMapping("/error-page")
    public String showErrorPage() {
        return "error-page";
    }
}




