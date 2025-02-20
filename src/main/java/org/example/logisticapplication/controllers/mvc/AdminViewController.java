package org.example.logisticapplication.controllers.mvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.Admin.CreateAdminDto;
import org.example.logisticapplication.service.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminViewController {
    private final AdminService adminService;

    @GetMapping("/registration")
    public String registration(Model model) {
        return "create-admin";
    }

    @PostMapping("/registration")
    public String createAdmin(
            @ModelAttribute CreateAdminDto adminDto,
            RedirectAttributes redirectAttributes
    ) {
        var savedAdmin = adminService.createAdmin(adminDto);
        var adminId = adminService.getAdminIdByEmail(savedAdmin.email());

        log.info("Admin id for redirect: {}", adminId);
        redirectAttributes.addFlashAttribute("message", "Пользователь успешно создан!");

        return "redirect:/admin/page/" + adminId;
    }

    @GetMapping("/page/{id}")
    public String adminPage(
            @PathVariable("id") Long id,
            Model model
    ) {
        var adminInfoById = adminService.getAdminInfoById(id);
        model.addAttribute("admin", adminInfoById);
        return "admin-panel";
    }

    @GetMapping("/orders")
    public String adminOrders() {
        return "orders";
    }

    @GetMapping("/orders/submit")
    public String submittingPage() {
        log.info("Get request for submitting orders");
        return "orders";
    }

}
