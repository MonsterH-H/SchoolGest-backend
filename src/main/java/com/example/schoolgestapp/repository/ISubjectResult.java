package com.example.schoolgestapp.repository;

import com.example.schoolgestapp.entity.SubjectResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ISubjectResult extends JpaRepository<SubjectResult, Long> {
}
