package com.example.SeatPlan.repository;

import com.example.SeatPlan.model.Hall;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HallRepository extends JpaRepository<Hall, Long> {
}