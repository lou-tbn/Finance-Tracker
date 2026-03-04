package com.dauphine.finance.controllers;

import com.dauphine.finance.DTO.DashboardResponse;
import com.dauphine.finance.services.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Tag(name = "Dashboard API", description = "Dashboard endpoints")
@RequestMapping("/v1/dashboard")
public class DashboardController {

    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @GetMapping("/{userId}")
    @Operation(
            summary = "Get dashboard for a user",
            description = "Returns total income/expense this month, balance, expenses by category and goal progress"
    )
    public ResponseEntity<DashboardResponse> getDashboard(@PathVariable UUID userId) {
        return ResponseEntity.ok(service.getDashboard(userId));
    }
}