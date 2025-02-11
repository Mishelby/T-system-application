package org.example.logisticapplication.controllers.mvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.service.DriverService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/drivers")
@RequiredArgsConstructor
public class DriverRegistrationViewController {
    private final DriverService driverService;

    @GetMapping("/registration")
    public String showDriverForm() {
        return "add-driver";
    }

    @GetMapping("/registration/success")
    public String successPage() {
        return "account-create-success";
    }

    @PostMapping("/registration")
    public String createDriver() {
        return "redirect:/driver/success";
    }

    @GetMapping("/{id}")
    public String showDriverInfoPage(
            @PathVariable("id") Long id,
            Model model
    ) {
        log.info("Get request for driver info with id={}", id);
        var driverInfo = driverService.getDriverInfo(id);

        if(driverInfo.getCurrentOrder()){
            model.addAttribute("driverInfo", driverInfo);
            return "driver-info";
        }else{
            model.addAttribute("driverInfo", driverInfo);
            return "driver-info-without-order";
        }
    }
}
