package org.example.logisticapplication.controllers.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/logistic")
public class StartPageViewController {
    @GetMapping("/welcome")
    public String startPage() {
        return "start-page";
    }
}
