package com.example.SeatPlan.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Entity
@Table(name = "halls")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hallId;

    @NotBlank(message = "Hall name is required")
    @Column(nullable = false)
    private String hallName;

    @Positive(message = "Rows must be greater than zero")
    @Column(nullable = false)
    private int rowsCount;

    @Positive(message = "Columns must be greater than zero")
    @Column(nullable = false)
    private int columnsCount;

    @Positive(message = "Capacity must be greater than zero")
    @Column(nullable = false)
    private int capacity;
}