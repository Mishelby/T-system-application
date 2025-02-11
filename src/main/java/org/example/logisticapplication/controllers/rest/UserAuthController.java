package org.example.logisticapplication.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.User.LoginUserDto;
import org.example.logisticapplication.service.UserService;
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
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Void> userLogin(
            @RequestBody LoginUserDto userDto
    ){
        log.info("Get request for login with email {}", userDto.email());
        var userDetails = userService.checkUserAccount(userDto);

        return  ResponseEntity.status(userDetails).build();
    }
}
