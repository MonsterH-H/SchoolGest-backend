package com.example.schoolgestapp.repository;

import com.example.schoolgestapp.entity.Specialite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ISpecialite  extends JpaRepository<Specialite, Long> {
}
