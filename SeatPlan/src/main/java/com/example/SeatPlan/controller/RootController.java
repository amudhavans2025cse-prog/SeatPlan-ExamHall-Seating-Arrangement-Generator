package com.example.SeatPlan.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RootController {

    @GetMapping("/api")
    public Map<String, Object> index() {
        return Map.of(
                "application", "SeatPlan",
                "status", "running",
                "endpoints", Map.of(
                        "halls", "/api/halls",
                        "students", "/api/students",
                        "exams", "/api/exams",
                        "allocations", "/api/allocations"
                )
        );
    }
}
