package com.example.SeatPlan.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "allocations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_exam_student",
                        columnNames = {"exam_id", "student_id"}
                ),
                @UniqueConstraint(
                        name = "unique_exam_seat",
                        columnNames = {"exam_id", "seat_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Allocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long allocationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private ExamSession examSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;
}