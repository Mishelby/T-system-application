package org.example.logisticapplication.controllers.mvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.Cargo.BaseCargoDto;
import org.example.logisticapplication.domain.Driver.DriverDefaultValues;
import org.example.logisticapplication.domain.RoutePoint.BaseRoutePoints;
import org.example.logisticapplication.domain.User.UserInfoDto;
import org.example.logisticapplication.repository.UserRepository;
import org.example.logisticapplication.service.CityService;
import org.example.logisticapplication.service.OrderService;
import org.example.logisticapplication.service.UserService;
import org.example.logisticapplication.utils.TimeFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderViewController {
    private final CityService cityService;
    private final DriverDefaultValues defaultValues;
    private final UserRepository userRepository;

    @GetMapping("/create")
    public String orders() {
        return "order";
    }

    @PostMapping("/create")
    public String createOrder(
            @RequestParam("id") Long id,
            Model model
    ) {
        var user = userRepository.findById(id).orElseThrow();
        var allCities = cityService.findAllCities();
        model.addAttribute("allCities", allCities);
        model.addAttribute("user", user);

        log.info("All cities: {} ", allCities);

        return "order";
    }

    @PostMapping("/submit")
    public String handleOrderSubmit(
            @RequestParam Long userId,
            @RequestParam String departureCity,
            @RequestParam String arrivalCity,
            @RequestParam Long cargoWeight,
            @RequestParam Long distance,
            Model model
    ) {

        log.info("Submitting order {}, {}, {}, {}", departureCity, arrivalCity, cargoWeight, distance);
        var baseCargoDto = new BaseCargoDto(cargoWeight);

        var user = userRepository.findById(userId).orElseThrow();

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

        var format = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        var orderForSubmittingDto = new OrderForSubmittingDto(
                new UserInfoDto(user.getUsername(), format),
                List.of(null)
        );

        model.addAttribute("calculatedTime", TimeFormatter.formatHours(time));
        model.addAttribute("orderDto", orderForSubmittingDto);

        return "order-summary";
    }

    @GetMapping("/success")
    public String showOrderSuccessPage(){
        return "order-success";
    }

}
