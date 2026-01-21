package com.example.schoolgestapp.repository;

import com.example.schoolgestapp.entity.Admin;
import com.example.schoolgestapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IAdmin extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUser(User user);
}
