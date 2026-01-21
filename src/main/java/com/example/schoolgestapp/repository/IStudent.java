package com.example.schoolgestapp.repository;

import com.example.schoolgestapp.entity.Student;
import com.example.schoolgestapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IStudent extends JpaRepository<Student, Long> {
    Optional<Student> findByUser(User user);
    java.util.List<com.example.schoolgestapp.entity.Student> findByClasseId(Long classeId);
    long countByClasseIdIn(java.util.Collection<Long> classeIds);
}
