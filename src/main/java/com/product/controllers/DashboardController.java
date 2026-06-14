package com.product.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.product.dto.DashboardDto;
import com.product.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<DashboardDto> getDashboard() {

        return ResponseEntity.ok(
                productService.getDashboardData()
        );
    }
}