package com.portfolio.controller;

import com.portfolio.view.response.PortfolioReportResponse;
import com.portfolio.service.PortfolioReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
@Tag(name = "Portfolio Report", description = "Portfolio report endpoints")
public class PortfolioReportController {

    private final PortfolioReportService reportService;

    @GetMapping("/report")
    @Operation(summary = "Generate portfolio report")
    public ResponseEntity<PortfolioReportResponse> generateReport() {
        return ResponseEntity.ok(reportService.generateReport());
    }
}
