package org.example.logisticapplication.controllers.mvc;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.Cargo.BaseCargoDto;
import org.example.logisticapplication.domain.Driver.DriverDefaultValues;
import org.example.logisticapplication.domain.Order.OrderRequestDto;
import org.example.logisticapplication.domain.Order.SendOrderForSubmittingDto;
import org.example.logisticapplication.domain.RoutePoint.BaseRoutePoints;
import org.example.logisticapplication.repository.UserRepository;
import org.example.logisticapplication.service.CityService;
import org.example.logisticapplication.service.OrderService;
import org.example.logisticapplication.utils.LocalDateTimeFormatter;
import org.example.logisticapplication.utils.TimeFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@Slf4j
@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderViewController {
    private final CityService cityService;
    private final DriverDefaultValues defaultValues;
    private final UserRepository userRepository;
    private final OrderService orderService;
    private final LocalDateTimeFormatter timeFormatter;

    private static final String USER_ID = "userId";

    @GetMapping("/create")
    public String orders() {
        return "order";
    }

    @PostMapping("/create")
    public String createOrder(
            @RequestParam("id") Long id,
            Model model,
            HttpSession session
    ) {
        var user = userRepository.findById(id).orElseThrow();
        var allCities = cityService.findAllCities();
        model.addAttribute("allCities", allCities);
        model.addAttribute(USER_ID, user.getId());

        session.setAttribute(USER_ID, user.getId());

        log.info("All cities: {} ", allCities);

        return "order";
    }

    @PostMapping("/submit")
    public String handleOrderSubmit(
            @RequestBody OrderRequestDto orderRequestDto,
            HttpSession session
    ) {
        orderService.isValidDateForOrder(orderRequestDto.departureDate());

        log.info("Submitting order {}", orderRequestDto);
        var sendOrderForSubmittingDto = getSendOrderForSubmittingDto(orderRequestDto);
        var orderDto = orderService.sendOrderForUser(sendOrderForSubmittingDto);

        var time = OrderService.calculationTimeToOrder(
                orderDto.routePointInfo().distance(),
                defaultValues.getAverageSpeed()
        );

        session.setAttribute("calculatedTime", TimeFormatter.formatHours(time));
        session.setAttribute("orderDto", orderDto);

        return "redirect:/orders/order-summary";
    }

    @GetMapping("/order-summary")
    public String orderSummary(Model model, HttpSession session) {
        model.addAttribute("calculatedTime", session.getAttribute("calculatedTime"));
        model.addAttribute("orderDto", session.getAttribute("orderDto"));

        return "order-summary";
    }

    @GetMapping("/success")
    public String showOrderSuccessPage(
            Model model,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute(USER_ID);
        model.addAttribute(USER_ID, userId);
        return "order-success";
    }

    private static SendOrderForSubmittingDto getSendOrderForSubmittingDto(
            OrderRequestDto orderRequestDto
    ) {
        var cargoDto = new BaseCargoDto(orderRequestDto.cargoWeight());
        var baseRoutePoints = new BaseRoutePoints(
                orderRequestDto.departureCity(),
                orderRequestDto.arrivalCity(),
                cargoDto,
                orderRequestDto.departureStations()
        );
        return new SendOrderForSubmittingDto(
                orderRequestDto.userId(),
                baseRoutePoints,
                orderRequestDto.departureDate()
        );
    }

}
