package com.example.schoolgestapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entité ModuleResult : Résultat d'un module spécifique dans un bulletin.
 */
@Entity
@Table(name = "module_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "report_card_id", nullable = false)
    private ReportCard reportCard;

    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    private Double average; // Moyenne calculée du module
    private Integer totalCredits; // Crédits ECTS validés pour ce module

    @OneToMany(mappedBy = "moduleResult", cascade = CascadeType.ALL)
    private List<SubjectResult> subjectResults;
}
