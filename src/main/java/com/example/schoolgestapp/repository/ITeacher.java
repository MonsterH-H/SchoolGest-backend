package com.example.schoolgestapp.repository;

import com.example.schoolgestapp.entity.Teacher;
import com.example.schoolgestapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ITeacher extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByUser(User user);
}
