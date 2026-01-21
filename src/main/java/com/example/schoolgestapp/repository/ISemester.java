package com.example.schoolgestapp.repository;

import com.example.schoolgestapp.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ISemester extends JpaRepository<Semester, Long> {
}
