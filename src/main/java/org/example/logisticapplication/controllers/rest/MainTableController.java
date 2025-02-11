package org.example.logisticapplication.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.service.MainTableService;
import org.example.logisticapplication.utils.MainTableInfoDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Slf4j
public class MainTableController {
    private final MainTableService mainTableService;

    @GetMapping
    public ResponseEntity<MainTableInfoDto> mainTable() {
        log.info("Get request for main table");
        var allInfo = mainTableService.getAllInfo();

        return ResponseEntity.ok()
                .body(allInfo);
    }
}
