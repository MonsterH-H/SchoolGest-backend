package com.example.schoolgestapp.repository;

import com.example.schoolgestapp.entity.AbsenceAlert;
import com.example.schoolgestapp.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAbsenceAlert extends JpaRepository<AbsenceAlert, Long> {
    List<AbsenceAlert> findByStudentOrderByCreatedAtDesc(Student student);
}
