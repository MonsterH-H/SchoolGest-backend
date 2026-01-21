package com.example.schoolgestapp.gestions_cahier_texte.controller;

import com.example.schoolgestapp.entity.CahierDeTexte;
import com.example.schoolgestapp.gestions_cahier_texte.dto.SeanceCreateDTO;
import com.example.schoolgestapp.gestions_cahier_texte.dto.SeanceResponseDTO;
import com.example.schoolgestapp.gestions_cahier_texte.services.CahierDeTexteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller pour le Cahier de Texte Numérique.
 */
@RestController
@RequestMapping("/api/cahier-texte")
@Tag(name = "10. Cahier de Texte Numérique", description = "Suivi pédagogique institutionnel, séances, devoirs et ressources")
public class CahierDeTexteController {

    private final CahierDeTexteService cahier;

    public CahierDeTexteController(CahierDeTexteService cahier) {
        this.cahier = cahier;
    }

    @GetMapping("/classe/{classeId}")
    @Operation(summary = "Consulter le cahier de texte complet d'une classe", 
               description = "Récupère toutes les séances enregistrées pour la classe spécifiée.")
    public ResponseEntity<List<SeanceResponseDTO>> getByClasse(@PathVariable Long classeId) {
        CahierDeTexte cdt = cahier.getOrCreateCahier(classeId);
        return ResponseEntity.ok(cahier.getSeancesByCahier(cdt.getId()));
    }

    @GetMapping("/classe/{classeId}/date/{date}")
    @Operation(summary = "Consulter les séances d'une classe pour un jour précis")
    public ResponseEntity<List<SeanceResponseDTO>> getByClasseAndDate(
            @PathVariable Long classeId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(cahier.getSeancesByClasseAndDate(classeId, date));
    }

    @PostMapping("/seances")
    @Operation(summary = "Ajouter une nouvelle séance pédagogique (Enseignant)",
               description = "Permet à un enseignant de contribuer au cahier de texte de la classe.")
    public ResponseEntity<SeanceResponseDTO> createSeance(@Valid @RequestBody SeanceCreateDTO dto) {
        return ResponseEntity.ok(cahier.createSeance(dto));
    }

    @PutMapping("/seances/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TEACHER')")
    @Operation(summary = "Modifier une séance existante (Seulement l'auteur)",
               description = "Seul l'enseignant qui a créé la séance peut la modifier.")
    public ResponseEntity<SeanceResponseDTO> updateSeance(
            @PathVariable Long id,
            @Valid @RequestBody SeanceCreateDTO dto,
            @RequestParam Long teacherId) {
        return ResponseEntity.ok(cahier.updateSeance(id, dto, teacherId));
    }

    @GetMapping("/seances/enseignant/{teacherId}")
    @Operation(summary = "Liste des séances saisies par un enseignant")
    public ResponseEntity<List<SeanceResponseDTO>> getByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(cahier.getSeancesByTeacher(teacherId));
    }

    @PutMapping("/{id}/archiver")
    @Operation(summary = "Archiver un cahier de texte (Action Admin)",
               description = "L'archivage verrouille le cahier de texte en fin d'année scolaire.")
    public ResponseEntity<Void> archiver(@PathVariable Long id) {
        cahier.archiverCahier(id);
        return ResponseEntity.noContent().build();
    }
}
