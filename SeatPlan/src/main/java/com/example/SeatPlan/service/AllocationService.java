package com.example.SeatPlan.service;

import com.example.SeatPlan.model.Allocation;
import com.example.SeatPlan.model.ExamSession;
import com.example.SeatPlan.model.Seat;
import com.example.SeatPlan.model.Student;
import com.example.SeatPlan.repository.AllocationRepository;
import com.example.SeatPlan.repository.ExamSessionRepository;
import com.example.SeatPlan.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AllocationService {

    private final AllocationRepository allocationRepository;
    private final ExamSessionRepository examRepository;
    private final SeatRepository seatRepository;

    public AllocationService(
            AllocationRepository allocationRepository,
            ExamSessionRepository examRepository,
            SeatRepository seatRepository) {

        this.allocationRepository = allocationRepository;
        this.examRepository = examRepository;
        this.seatRepository = seatRepository;
    }

    @Transactional
    public List<Allocation> generateSeating(Long examId) {

        ExamSession exam =
                examRepository.findById(examId)
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "Exam not found with ID: " + examId
                                ));

        List<Student> students =
                exam.getStudents();

        if (students == null || students.isEmpty()) {

            throw new IllegalArgumentException(
                    "No students registered for this exam."
            );
        }

        List<Seat> allSeats =
                seatRepository.findAll();

        List<Allocation> existingAllocations =
                allocationRepository
                        .findByExamSessionExamId(examId);

        if (!existingAllocations.isEmpty()) {

            throw new IllegalStateException(
                    "Seating has already been generated "
                            + "for this examination. "
                            + "Clear the existing allocation first."
            );
        }

        if (students.size() > allSeats.size()) {

            throw new IllegalStateException(
                    "Not enough seats available. "
                            + "Students: "
                            + students.size()
                            + ", Available seats: "
                            + allSeats.size()
            );
        }

        List<Seat> availableSeats =
                new ArrayList<>(allSeats);

        List<Allocation> allocations =
                new ArrayList<>();

        for (Student student : students) {

            Seat bestSeat = findBestSeat(
                    student,
                    availableSeats,
                    allocations
            );

            if (bestSeat == null) {

                throw new IllegalStateException(
                        "Unable to find a suitable seat "
                                + "for student: "
                                + student.getRegisterNumber()
                );
            }

            Allocation allocation =
                    Allocation.builder()
                            .examSession(exam)
                            .student(student)
                            .seat(bestSeat)
                            .build();

            allocations.add(allocation);

            availableSeats.remove(bestSeat);
        }

        return allocationRepository.saveAll(
                allocations
        );
    }

    private Seat findBestSeat(
            Student student,
            List<Seat> availableSeats,
            List<Allocation> currentAllocations) {

        Seat bestSeat = null;

        int lowestConflict =
                Integer.MAX_VALUE;

        for (Seat seat : availableSeats) {

            int conflict =
                    calculateConflict(
                            student,
                            seat,
                            currentAllocations
                    );

            if (conflict < lowestConflict) {

                lowestConflict = conflict;
                bestSeat = seat;
            }
        }

        return bestSeat;
    }

    private int calculateConflict(
            Student student,
            Seat candidateSeat,
            List<Allocation> allocations) {

        int conflict = 0;

        for (Allocation allocation : allocations) {

            Seat occupiedSeat =
                    allocation.getSeat();

            if (areAdjacent(
                    candidateSeat,
                    occupiedSeat)) {

                Student occupiedStudent =
                        allocation.getStudent();

                if (sameSection(
                        student,
                        occupiedStudent)) {

                    conflict++;
                }
            }
        }

        return conflict;
    }

    private boolean sameSection(
            Student student1,
            Student student2) {

        return student1.getClassName()
                .equalsIgnoreCase(
                        student2.getClassName()
                )
                &&
                student1.getSection()
                        .equalsIgnoreCase(
                                student2.getSection()
                        );
    }

    private boolean areAdjacent(
            Seat seat1,
            Seat seat2) {

        if (!seat1.getHall()
                .getHallId()
                .equals(
                        seat2.getHall().getHallId()
                )) {

            return false;
        }

        int rowDifference =
                Math.abs(
                        seat1.getRowNumber()
                                - seat2.getRowNumber()
                );

        int columnDifference =
                Math.abs(
                        seat1.getColumnNumber()
                                - seat2.getColumnNumber()
                );

        /*
         * Horizontal or vertical adjacency only.
         *
         * Example:
         *
         * A B
         * C D
         *
         * A-B and A-C are adjacent.
         * A-D is diagonal and therefore not adjacent.
         */

        return
                (rowDifference == 1
                        && columnDifference == 0)
                ||
                (rowDifference == 0
                        && columnDifference == 1);
    }

    public List<Allocation> getExamAllocations(
            Long examId) {

        if (!examRepository.existsById(examId)) {

            throw new NoSuchElementException(
                    "Exam not found with ID: " + examId
            );
        }

        return allocationRepository
                .findByExamSessionExamId(examId);
    }

    public Allocation getStudentAllocation(
            String registerNumber,
            Long examId) {

        List<Allocation> allocations =
                allocationRepository
                        .findByExamSessionExamId(examId);

        return allocations.stream()
                .filter(allocation ->
                        allocation.getStudent()
                                .getRegisterNumber()
                                .equalsIgnoreCase(
                                        registerNumber
                                ))
                .findFirst()
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "No seat allocation found for "
                                        + registerNumber
                        ));
    }

    @Transactional
    public void clearExamAllocation(Long examId) {

        if (!examRepository.existsById(examId)) {

            throw new NoSuchElementException(
                    "Exam not found with ID: " + examId
            );
        }

        allocationRepository
                .deleteByExamSessionExamId(examId);
    }
}