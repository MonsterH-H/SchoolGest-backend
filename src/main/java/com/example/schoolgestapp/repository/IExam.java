package com.example.schoolgestapp.repository;

import com.example.schoolgestapp.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IExam extends JpaRepository<Exam, Long> {
}
