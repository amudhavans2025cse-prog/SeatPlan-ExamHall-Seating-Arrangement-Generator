package com.example.SeatPlan.repository;

import com.example.SeatPlan.model.ExamSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamSessionRepository
        extends JpaRepository<ExamSession, Long> {
}