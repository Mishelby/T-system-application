package org.example.logisticapplication.controllers.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/driver")
public class DriverViewController {

    @GetMapping("/create")
    public String showDriverForm() {
        return "add-driver";
    }

    @PostMapping("/create")
    public String createDriver() {
        return "add-driver";
    }

}
