package com.example.SeatPlan.service;

import com.example.SeatPlan.model.Student;
import com.example.SeatPlan.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {

        if (studentRepository.existsByRegisterNumber(
                student.getRegisterNumber())) {

            throw new IllegalArgumentException(
                    "Register number already exists: "
                            + student.getRegisterNumber()
            );
        }

        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {

        return studentRepository.findById(id)
                .orElseThrow();
    }

    public Student getStudentByRegisterNumber(
            String registerNumber) {

        return studentRepository
                .findByRegisterNumber(registerNumber)
                .orElseThrow();
    }

    public Student updateStudent(
            Long id,
            Student updatedStudent) {

        Student student = getStudentById(id);

        student.setStudentName(
                updatedStudent.getStudentName());

        student.setClassName(
                updatedStudent.getClassName());

        student.setSection(
                updatedStudent.getSection());

        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {

        Student student = getStudentById(id);

        studentRepository.delete(student);
    }
}