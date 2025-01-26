package org.example.logisticapplication.controllers.mvc;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.controllers.rest.UserParamDto;
import org.example.logisticapplication.repository.DriverRepository;
import org.example.logisticapplication.service.RegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/driver")
@RequiredArgsConstructor
public class LoginViewController {
    private final RegistrationService registrationService;
    private static final String LOGIN_PAGE = "login-page";

    @GetMapping("/login")
    public String showLoginPage() {
        return LOGIN_PAGE;
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String userName,
            @RequestParam Long userNumber,
            HttpSession session
    ) {
        log.info("Attempting login for userName={}, userNumber={}", userName, userNumber);

        var userParamDto = new UserParamDto(userName, userNumber);

        try {
            if (registrationService.checkUserData(userParamDto)) {
                var driverId = registrationService.getDriverIdByUserNumber(userNumber);
                return "redirect:/driver/" + driverId;
            } else {
                session.setAttribute("errorMessage", "Неверные имя или номер пользователя");
                return "redirect:/driver/error-page";
            }
        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage());
            session.setAttribute("errorMessage", "Ошибка сервера. Попробуйте позже.");
            return "redirect:/driver/error-page";
        }
    }
}



