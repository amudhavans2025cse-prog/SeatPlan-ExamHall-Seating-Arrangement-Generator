package com.example.SeatPlan.controller;

import com.example.SeatPlan.model.ExamSession;
import com.example.SeatPlan.service.ExamSessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
public class ExamSessionController {

    private final ExamSessionService examService;

    public ExamSessionController(
            ExamSessionService examService) {

        this.examService = examService;
    }

    @PostMapping
    public ResponseEntity<ExamSession> createExam(
            @Valid @RequestBody ExamSession exam) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        examService.createExam(exam)
                );
    }

    @GetMapping
    public ResponseEntity<List<ExamSession>>
    getAllExams() {

        return ResponseEntity.ok(
                examService.getAllExams()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamSession> getExam(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                examService.getExamById(id)
        );
    }

    @PostMapping("/{examId}/students")
    public ResponseEntity<ExamSession>
    addStudentsToExam(
            @PathVariable Long examId,
            @RequestBody List<Long> studentIds) {

        return ResponseEntity.ok(
                examService.addStudentsToExam(
                        examId,
                        studentIds
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExamSession> updateExam(
            @PathVariable Long id,
            @Valid @RequestBody ExamSession exam) {

        return ResponseEntity.ok(
                examService.updateExam(id, exam)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExam(
            @PathVariable Long id) {

        examService.deleteExam(id);

        return ResponseEntity.ok(
                "Exam deleted successfully."
        );
    }
}