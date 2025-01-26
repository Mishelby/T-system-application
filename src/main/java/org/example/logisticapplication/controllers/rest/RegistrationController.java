package org.example.logisticapplication.controllers.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.service.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;

    @PostMapping("/login")
    public ResponseEntity<Boolean> checkRegistration(
            @RequestBody @Valid UserParamDto userParamDto
    ) {
        log.info("User data for checking: {}", userParamDto);

        return ResponseEntity.ok()
                .body(registrationService.checkUserData(
                        userParamDto
                ));
    }
}
