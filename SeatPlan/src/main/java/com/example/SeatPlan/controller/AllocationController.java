package com.example.SeatPlan.controller;

import com.example.SeatPlan.model.Allocation;
import com.example.SeatPlan.service.AllocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/allocations")
public class AllocationController {

    private final AllocationService allocationService;

    public AllocationController(
            AllocationService allocationService) {

        this.allocationService = allocationService;
    }

    @PostMapping("/generate/{examId}")
    public ResponseEntity<List<Allocation>>
    generateSeating(
            @PathVariable Long examId) {

        return ResponseEntity.ok(
                allocationService
                        .generateSeating(examId)
        );
    }

    @GetMapping("/exam/{examId}")
    public ResponseEntity<List<Allocation>>
    getExamAllocations(
            @PathVariable Long examId) {

        return ResponseEntity.ok(
                allocationService
                        .getExamAllocations(examId)
        );
    }

    @GetMapping("/student/{registerNumber}")
    public ResponseEntity<Allocation>
    getStudentAllocation(
            @PathVariable String registerNumber,
            @RequestParam Long examId) {

        return ResponseEntity.ok(
                allocationService
                        .getStudentAllocation(
                                registerNumber,
                                examId
                        )
        );
    }

    @DeleteMapping("/exam/{examId}")
    public ResponseEntity<String>
    clearExamAllocation(
            @PathVariable Long examId) {

        allocationService
                .clearExamAllocation(examId);

        return ResponseEntity.ok(
                "Exam seating allocation cleared successfully."
        );
    }
}