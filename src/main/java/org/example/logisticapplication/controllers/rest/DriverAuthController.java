package org.example.logisticapplication.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.Driver.DriverLoginInfo;
import org.example.logisticapplication.service.DriverService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth/drivers")
@RequiredArgsConstructor
public class DriverAuthController {
    private final DriverService driverService;

    @PostMapping("/login")
    public ResponseEntity<Void> driverLogin(
            @RequestBody DriverLoginInfo loginInfo
    ){
        log.info("Post request for login info: {}", loginInfo);
        var httpStatus = driverService.driverLogin(loginInfo);

        return ResponseEntity.status(httpStatus).build();
    }
}
