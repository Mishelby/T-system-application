package org.example.logisticapplication.controllers.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/registration")
public class RegistrationViewController {

    @GetMapping("/register-driver")
    public String redirectToDriverCreation() {
        return "redirect:/driver/create";
    }

}
