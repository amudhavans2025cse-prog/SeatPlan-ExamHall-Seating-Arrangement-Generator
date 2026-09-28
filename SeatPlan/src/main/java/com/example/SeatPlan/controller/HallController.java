package com.example.SeatPlan.controller;

import com.example.SeatPlan.model.Hall;
import com.example.SeatPlan.service.HallService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/halls")
public class HallController {

    private final HallService hallService;

    public HallController(HallService hallService) {
        this.hallService = hallService;
    }

    @PostMapping
    public ResponseEntity<Hall> createHall(
            @Valid @RequestBody Hall hall) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(hallService.createHall(hall));
    }

    @GetMapping
    public ResponseEntity<List<Hall>> getAllHalls() {

        return ResponseEntity.ok(
                hallService.getAllHalls()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hall> getHall(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                hallService.getHallById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Hall> updateHall(
            @PathVariable Long id,
            @Valid @RequestBody Hall hall) {

        return ResponseEntity.ok(
                hallService.updateHall(id, hall)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteHall(
            @PathVariable Long id) {

        hallService.deleteHall(id);

        return ResponseEntity.ok(
                "Hall deleted successfully."
        );
    }
}