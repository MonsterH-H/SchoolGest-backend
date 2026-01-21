package com.example.schoolgestapp.repository;

import com.example.schoolgestapp.entity.Classe;
import com.example.schoolgestapp.entity.Enrollment;
import com.example.schoolgestapp.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IEnrollment extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudent(Student student);
    List<Enrollment> findByClasseIdAndAcademicYear(Long classeId, String academicYear);
    Optional<Enrollment> findByStudentAndClasse(Student student, Classe classe);
}
