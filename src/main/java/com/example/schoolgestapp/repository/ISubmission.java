package com.example.schoolgestapp.repository;

import com.example.schoolgestapp.entity.Assignment;
import com.example.schoolgestapp.entity.Student;
import com.example.schoolgestapp.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ISubmission extends JpaRepository<Submission, Long> {
    List<Submission> findByAssignment(Assignment assignment);
    List<Submission> findByStudent(Student student);
    Optional<Submission> findByAssignmentAndStudent(Assignment assignment, Student student);
    long countByAssignment_Teacher_IdAndGradeIsNull(Long teacherId);
}
