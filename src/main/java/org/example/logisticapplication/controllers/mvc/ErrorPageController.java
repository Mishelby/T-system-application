package org.example.logisticapplication.controllers.mvc;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/driver")
public class ErrorPageController {
    private static final String ERROR_MESSAGE_KEY = "errorMessage";

    @GetMapping("/error-page")
    public String showErrorPage(
            Model model,
            HttpSession session
    ) {
        var errorMessage = (String) session.getAttribute(ERROR_MESSAGE_KEY);
        if (errorMessage != null) {
            model.addAttribute(ERROR_MESSAGE_KEY, errorMessage);
            session.removeAttribute(ERROR_MESSAGE_KEY);
        }
        return "error-page";
    }
}
