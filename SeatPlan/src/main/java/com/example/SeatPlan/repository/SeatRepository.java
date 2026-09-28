package com.example.SeatPlan.repository;

import com.example.SeatPlan.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByHallHallId(Long hallId);
}