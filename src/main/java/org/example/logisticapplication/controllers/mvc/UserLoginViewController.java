package org.example.logisticapplication.controllers.mvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserLoginViewController {
    private static final String USER_LOGIN_PAGE = "login-page";

    @GetMapping("/login")
    public String showLoginPageUser() {
        return USER_LOGIN_PAGE;
    }
}
