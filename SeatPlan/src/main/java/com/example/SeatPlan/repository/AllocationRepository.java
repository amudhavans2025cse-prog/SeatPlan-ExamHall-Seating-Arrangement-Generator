package com.example.SeatPlan.repository;

import com.example.SeatPlan.model.Allocation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AllocationRepository
        extends JpaRepository<Allocation, Long> {

    List<Allocation> findByExamSessionExamId(Long examId);

    Optional<Allocation> findByExamSessionExamIdAndStudentStudentId(
            Long examId,
            Long studentId
    );

    boolean existsByExamSessionExamIdAndStudentStudentId(
            Long examId,
            Long studentId
    );

    boolean existsByExamSessionExamIdAndSeatSeatId(
            Long examId,
            Long seatId
    );

    void deleteByExamSessionExamId(Long examId);
}