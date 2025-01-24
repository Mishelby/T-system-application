package org.example.logisticapplication.controllers.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BusinessesLogicController {
    @GetMapping("/add-truck")
    public String addTruckForDriver(){
        return "add-truck-for-driver";
    }
}
