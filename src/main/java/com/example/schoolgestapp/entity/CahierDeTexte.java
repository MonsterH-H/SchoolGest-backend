package com.example.schoolgestapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Cahier de texte institutionnel, lié à une classe.
 */
@Entity
@Table(name = "cahiers_de_texte")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CahierDeTexte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "classe_id", unique = true, nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"department", "responsible", "subGroups"})
    private Classe classe;

    @Column(nullable = false)
    private Boolean archive = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime creeLe;
}
