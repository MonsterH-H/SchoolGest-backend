package com.example.schoolgestapp.repository;

import com.example.schoolgestapp.entity.ReportCard;
import com.example.schoolgestapp.entity.Student;
import com.example.schoolgestapp.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IReportCard extends JpaRepository<ReportCard, Long> {
    List<ReportCard> findByStudent(Student student);
    Optional<ReportCard> findByStudentAndSemesterAndAcademicYear(Student student, Semester semester, String academicYear);
    List<ReportCard> findBySemesterAndAcademicYear(Semester semester, String academicYear);
}
