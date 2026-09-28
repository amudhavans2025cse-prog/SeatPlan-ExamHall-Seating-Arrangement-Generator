package com.example.SeatPlan.service;

import com.example.SeatPlan.model.Hall;
import com.example.SeatPlan.model.Seat;
import com.example.SeatPlan.repository.HallRepository;
import com.example.SeatPlan.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HallService {

    private final HallRepository hallRepository;
    private final SeatRepository seatRepository;

    public HallService(
            HallRepository hallRepository,
            SeatRepository seatRepository) {

        this.hallRepository = hallRepository;
        this.seatRepository = seatRepository;
    }

    @Transactional
    public Hall createHall(Hall hall) {

        int capacity =
                hall.getRowsCount() * hall.getColumnsCount();

        hall.setCapacity(capacity);

        Hall savedHall = hallRepository.save(hall);

        for (int row = 1;
             row <= savedHall.getRowsCount();
             row++) {

            for (int column = 1;
                 column <= savedHall.getColumnsCount();
                 column++) {

                Seat seat = Seat.builder()
                        .hall(savedHall)
                        .rowNumber(row)
                        .columnNumber(column)
                        .seatNumber(
                                "R" + row + "C" + column
                        )
                        .build();

                seatRepository.save(seat);
            }
        }

        return savedHall;
    }

    public List<Hall> getAllHalls() {
        return hallRepository.findAll();
    }

    public Hall getHallById(Long id) {

        return hallRepository.findById(id)
                .orElseThrow();
    }

    @Transactional
    public Hall updateHall(Long id, Hall updatedHall) {

        Hall hall = getHallById(id);

        hall.setHallName(updatedHall.getHallName());
        hall.setRowsCount(updatedHall.getRowsCount());
        hall.setColumnsCount(updatedHall.getColumnsCount());

        int capacity =
                updatedHall.getRowsCount()
                        * updatedHall.getColumnsCount();

        hall.setCapacity(capacity);

        return hallRepository.save(hall);
    }

    @Transactional
    public void deleteHall(Long id) {

        Hall hall = getHallById(id);

        List<Seat> seats =
                seatRepository.findByHallHallId(id);

        seatRepository.deleteAll(seats);

        hallRepository.delete(hall);
    }
}