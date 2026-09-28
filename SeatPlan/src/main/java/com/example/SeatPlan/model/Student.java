package com.example.SeatPlan.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;

    @NotBlank(message = "Register number is required")
    @Column(nullable = false, unique = true)
    private String registerNumber;

    @NotBlank(message = "Student name is required")
    @Column(nullable = false)
    private String studentName;

    @NotBlank(message = "Class name is required")
    @Column(nullable = false)
    private String className;

    @NotBlank(message = "Section is required")
    @Column(nullable = false)
    private String section;
}