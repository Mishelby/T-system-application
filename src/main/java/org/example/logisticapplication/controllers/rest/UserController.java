package org.example.logisticapplication.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.User.CreateUserDto;
import org.example.logisticapplication.domain.User.LoginUserDto;
import org.example.logisticapplication.domain.User.MainUserInfoDro;
import org.example.logisticapplication.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<MainUserInfoDro> getUserInfoById(
            @PathVariable("id") Long id
    ) {
        log.info("Get request for user info by id: {}", id);
        var userInfo = userService.getUserInfo(id);

        return ResponseEntity.ok().body(userInfo);
    }

    @PostMapping("/registration")
    public ResponseEntity<CreateUserDto> createUser(
            @RequestBody CreateUserDto newUser
    ) {
        log.info("Get request for create new user: {}", newUser);
        var savedUser = userService.createNewUser(newUser);

        return ResponseEntity.ok().body(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @RequestBody LoginUserDto userDto
    ){
        log.info("Get request for login with email {}", userDto.email());
        var userDetails = userService.checkUserAccount(userDto);

        return  ResponseEntity.status(HttpStatus.OK).build();
    }
}
