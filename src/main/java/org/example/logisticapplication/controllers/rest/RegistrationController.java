package org.example.logisticapplication.controllers.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.service.RegistrationService;
import org.example.logisticapplication.utils.UserParamDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;

    @PostMapping("/login")
    public ResponseEntity<String> checkRegistration(
            @RequestBody @Valid UserParamDto userParamDto
    ) {
        log.info("User data for checking: {}", userParamDto);
        var httpStatus = registrationService.checkUserData(userParamDto);

        return ResponseEntity.status(httpStatus).build();
    }

    @PostMapping("/current-id")
    public ResponseEntity<Long> getCurrentId(
            @RequestBody Long userNumber
    ) {
        log.info("Get current id for user number: {}", userNumber);
        var driverId = registrationService.getDriverIdByUserNumber(userNumber);

        return ResponseEntity.ok().body(driverId);
    }

}
