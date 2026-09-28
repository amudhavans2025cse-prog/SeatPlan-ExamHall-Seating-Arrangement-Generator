package com.example.SeatPlan.service;

import com.example.SeatPlan.model.ExamSession;
import com.example.SeatPlan.model.Student;
import com.example.SeatPlan.repository.ExamSessionRepository;
import com.example.SeatPlan.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExamSessionService {

    private final ExamSessionRepository examRepository;
    private final StudentRepository studentRepository;

    public ExamSessionService(
            ExamSessionRepository examRepository,
            StudentRepository studentRepository) {

        this.examRepository = examRepository;
        this.studentRepository = studentRepository;
    }

    public ExamSession createExam(
            ExamSession examSession) {

        return examRepository.save(examSession);
    }

    public List<ExamSession> getAllExams() {
        return examRepository.findAll();
    }

    public ExamSession getExamById(Long id) {

        return examRepository.findById(id)
                .orElseThrow();
    }

    @Transactional
    public ExamSession addStudentsToExam(
            Long examId,
            List<Long> studentIds) {

        ExamSession exam = getExamById(examId);

        List<Student> students =
                new ArrayList<>();

        for (Long studentId : studentIds) {

            Student student =
                    studentRepository.findById(studentId)
                            .orElseThrow();


            if (!exam.getStudents().contains(student)) {
                students.add(student);
            }
        }

        exam.getStudents().addAll(students);

        return examRepository.save(exam);
    }

    public ExamSession updateExam(
            Long id,
            ExamSession updatedExam) {

        ExamSession exam = getExamById(id);

        exam.setExamName(updatedExam.getExamName());
        exam.setExamDate(updatedExam.getExamDate());
        exam.setStartTime(updatedExam.getStartTime());
        exam.setEndTime(updatedExam.getEndTime());

        return examRepository.save(exam);
    }

    public void deleteExam(Long id) {

        ExamSession exam = getExamById(id);

        examRepository.delete(exam);
    }
}