package org.example.logisticapplication.controllers.mvc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/driver")
public class DriverViewController {
    @GetMapping("/registration")
    public String showDriverForm() {
        return "add-driver";
    }

    @GetMapping("/success")
    public String successPage() {
        return "account-create-success";
    }

    @PostMapping("/registration")
    public String createDriver() {
        return "redirect:/driver/success";
    }
}

