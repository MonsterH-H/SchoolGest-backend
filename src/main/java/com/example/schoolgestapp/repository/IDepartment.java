package com.example.schoolgestapp.repository;

import com.example.schoolgestapp.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IDepartment extends JpaRepository<Department, Long> {
    Optional<Department> findByCode(String code);
    List<Department> findByEstablishmentId(Long establishmentId);
}
