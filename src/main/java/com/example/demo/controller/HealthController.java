package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Useful when deploying to know it's up
 */
@RestController
public class HealthController {
    @GetMapping("/health")
    public String health() {
        return "ok";
    }
}
