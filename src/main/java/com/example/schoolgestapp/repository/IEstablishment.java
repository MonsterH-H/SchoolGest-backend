package com.example.schoolgestapp.repository;

import com.example.schoolgestapp.entity.Establishment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IEstablishment extends JpaRepository<Establishment, Long> {
    Optional<Establishment> findByCode(String code);
}
