package org.example.logisticapplication.controllers.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/route-points")
public class RoutePointsViewController {

    @GetMapping("/add")
    public String showAddCargoPage() {
        return "cargo";
    }
}