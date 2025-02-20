package org.example.logisticapplication.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.User.CreateUserDto;
import org.example.logisticapplication.domain.User.MainUserInfoWithoutOrdersDto;
import org.example.logisticapplication.domain.User.UserInfo;
import org.example.logisticapplication.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<CreateUserDto> createUser(
            @RequestBody CreateUserDto newUser
    ) {
        log.info("Get request for create new user: {}", newUser);
        var savedUser = userService.createNewUser(newUser);
        log.info("Saved user!: {}", savedUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserInfo> getUserInfoById(
            @PathVariable("id") Long id
    ) {
        log.info("Get request for user info by id: {}", id);
        var userInfo = userService.getUserInfo(id);

        return ResponseEntity.ok().body(userInfo);
    }
}
