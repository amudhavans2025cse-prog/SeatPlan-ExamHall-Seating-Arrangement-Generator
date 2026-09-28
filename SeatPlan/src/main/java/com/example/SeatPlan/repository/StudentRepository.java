package com.example.SeatPlan.repository;

import com.example.SeatPlan.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByRegisterNumber(String registerNumber);

    boolean existsByRegisterNumber(String registerNumber);
}