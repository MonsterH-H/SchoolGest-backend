package com.example.schoolgestapp.repository;

import com.example.schoolgestapp.entity.SeancePedagogique;
import com.example.schoolgestapp.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ISeancePedagogique extends JpaRepository<SeancePedagogique, Long> {
    List<SeancePedagogique> findByCahierDeTexteId(Long cahierDeTexteId);
    List<SeancePedagogique> findByCahierDeTexteIdAndDate(Long cahierDeTexteId, LocalDate date);
    List<SeancePedagogique> findByEnseignantId(Long teacherId);
    List<SeancePedagogique> findByEnseignant(Teacher teacher);
}
