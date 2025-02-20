package org.example.logisticapplication.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.Admin.BaseAdminDto;
import org.example.logisticapplication.domain.Admin.CreateAdminDto;
import org.example.logisticapplication.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<BaseAdminDto> createAdmin(
            @RequestBody CreateAdminDto createAdminDto
    ){
        log.info("Ger request for creating new admin, {}", createAdminDto);
        var admin = adminService.createAdmin(createAdminDto);

        return ResponseEntity.ok().body(admin);
    }
}
