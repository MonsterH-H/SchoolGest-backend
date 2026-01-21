package com.example.schoolgestapp.repository;

import com.example.schoolgestapp.entity.Grade;
import com.example.schoolgestapp.entity.Student;
import com.example.schoolgestapp.entity.Subject;
import com.example.schoolgestapp.entity.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IGrade extends JpaRepository<Grade, Long> {
    List<Grade> findByStudent(Student student);
    List<Grade> findBySubject(Subject subject);
    List<Grade> findByStudentAndSubject(Student student, Subject subject);
    List<Grade> findBySubjectAndEvaluationType(Subject subject, com.example.schoolgestapp.entity.enums.EvaluationType type);
    List<Grade> findByStatus(Status status);
}
