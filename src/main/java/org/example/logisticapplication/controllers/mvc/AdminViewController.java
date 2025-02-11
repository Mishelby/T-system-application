package org.example.logisticapplication.controllers.mvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminViewController {
    private final OrderService orderService;

    @GetMapping("/page")
    public String adminPage() {
        return "admin-panel";
    }

    @GetMapping("/orders/submit")
    public String submittingPage(Model model) {
        log.info("Get request for submitting orders");
        return "orders";
    }

}
