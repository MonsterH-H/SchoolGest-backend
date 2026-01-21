package com.example.schoolgestapp.repository;

import com.example.schoolgestapp.entity.Salle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISalle extends JpaRepository<Salle, Long> {
    List<Salle> findByActif(boolean actif);
    List<Salle> findByType(String type);
}
