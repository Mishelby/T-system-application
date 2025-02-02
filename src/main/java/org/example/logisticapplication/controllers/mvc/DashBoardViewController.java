package org.example.logisticapplication.controllers.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/dashboard")
public class DashBoardViewController {

    @GetMapping
    public String dashboard() {
        return "dashboard";
    }
}
