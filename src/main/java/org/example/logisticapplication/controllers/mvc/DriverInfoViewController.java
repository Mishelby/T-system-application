package org.example.logisticapplication.controllers.mvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.service.DriverService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/driver")
@RequiredArgsConstructor
public class DriverInfoViewController {
    private final DriverService driverService;

    @GetMapping("/{id}")
    public String showDriverInfoPage(
            @PathVariable("id") Long id,
            Model model
    ) {
        log.info("Fetching driver info for id={}", id);
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


