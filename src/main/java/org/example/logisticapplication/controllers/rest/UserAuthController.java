package org.example.logisticapplication.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.User.LoginUserDto;
import org.example.logisticapplication.service.UserAuthenticationService;
import org.example.logisticapplication.utils.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserAuthController {
    private final UserAuthenticationService userAuthenticationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> userLogin(
            @RequestBody LoginUserDto userDto
    ){
        log.info("Get request for login with username {}", userDto.username());
        var response = userAuthenticationService.authenticate(userDto);
        log.info("User response after authenticate {}", response);

        return ResponseEntity.ok().body(response);
    }
}
