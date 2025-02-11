package org.example.logisticapplication.controllers.mvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.Cargo.BaseCargoDto;
import org.example.logisticapplication.domain.Driver.DriverDefaultValues;
import org.example.logisticapplication.domain.RoutePoint.BaseRoutePoints;
import org.example.logisticapplication.service.CityService;
import org.example.logisticapplication.service.OrderService;
import org.example.logisticapplication.utils.TimeFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderViewController {
    private final CityService cityService;
    private final DriverDefaultValues defaultValues;

    @GetMapping("/create")
    public String showOrderPage(
            Model model
    ) {
        var allCities = cityService.findAllCities();
        model.addAttribute("allCities", allCities);

        log.info("All cities: {} ", allCities);

        return "order";
    }

    @PostMapping("/submit")
    public String handleOrderSubmit(
            @RequestParam String departureCity,
            @RequestParam String arrivalCity,
            @RequestParam Long cargoWeight,
            @RequestParam Long distance,
            Model model
    ) {

        log.info("Submitting order {}, {}, {}, {}", departureCity, arrivalCity, cargoWeight, distance);
        var baseCargoDto = new BaseCargoDto(cargoWeight);

        var time = OrderService.calculationTimeToOrder(
                distance,
                defaultValues.getAverageSpeed()
        );

        var baseRoutePoints = new BaseRoutePoints(
                departureCity,
                arrivalCity,
                baseCargoDto,
                distance
        );

        model.addAttribute("calculatedTime", TimeFormatter.formatHours(time));
        model.addAttribute("orderDto", baseRoutePoints);

        return "order-summary";
    }

    @GetMapping("/success")
    public String showOrderSuccessPage(){
        return "order-success";
    }

}
