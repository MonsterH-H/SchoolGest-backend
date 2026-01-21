package com.example.schoolgestapp.repository;

import com.example.schoolgestapp.entity.CahierDeTexte;
import com.example.schoolgestapp.entity.Classe;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ICahierDeTexte extends JpaRepository<CahierDeTexte, Long> {
    Optional<CahierDeTexte> findByClasseId(Long classeId);
    Optional<CahierDeTexte> findByClasse(Classe classe);
}
