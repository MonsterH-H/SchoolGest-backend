package com.example.schoolgestapp.gestions_academique.controller;

import com.example.schoolgestapp.entity.ReportCard;
import com.example.schoolgestapp.gestions_academique.dto.ReportCardDTO;
import com.example.schoolgestapp.gestions_academique.services.ReportCardPdfService;
import com.example.schoolgestapp.gestions_academique.services.ReportCardService;
import com.example.schoolgestapp.repository.IReportCard;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bulletins")
@Tag(name = "06. Bulletins et resultats", description = "Gestion de notes semestrielles, calcul des rangs et exports PDF professionnels")
public class ReportCardController {

    private final ReportCardService reportCardService;
    private final ReportCardPdfService pdfService;
    private final IReportCard reportCardRepository;

    public ReportCardController(ReportCardService reportCardService, 
                                ReportCardPdfService pdfService, 
                                IReportCard reportCardRepository) {
        this.reportCardService = reportCardService;
        this.pdfService = pdfService;
        this.reportCardRepository = reportCardRepository;
    }

    /**
     * Générer le bulletin d'un etudiant.
     */
    @Operation(summary = "Calculer le bulletin", description = "Calcule toutes les moyennes et le rang d'un etudiant. Si studentId est omis, génère pour tous les étudiants.")
    @PostMapping("/generer")
    public ResponseEntity<Object> generate(@RequestBody Map<String, Object> body) {
        Long semesterId = requireLong(body, "semesterId");
        Long studentId = body.containsKey("studentId") ? requireLong(body, "studentId") : null;
        
        // Si academicYear n'est pas fourni, on le déduit du semestre
        String year = body.containsKey("academicYear") ? requireString(body, "academicYear") : null;
        
        if (studentId != null) {
            return ResponseEntity.ok(reportCardService.generateReportCard(studentId, semesterId, year));
        } else {
            reportCardService.generateAllReportCards(semesterId, year);
            return ResponseEntity.ok(Map.of("message", "Génération globale lancée avec succès"));
        }
    }

    /**
     * Calculer les rangs pour une classe entière.
     */
    @Operation(summary = "Calculer les rangs", description = "Met à jour le classement de tous les élèves d'un semestre")
    @PostMapping("/calculer-rangs")
    public ResponseEntity<Void> calculateRanks(@RequestBody Map<String, Object> body) {
        Long semesterId = requireLong(body, "semesterId");
        String year = body.containsKey("academicYear") ? requireString(body, "academicYear") : null;
        
        reportCardService.calculateClassRanks(semesterId, year);
        return ResponseEntity.ok().build();
    }

    /**
     * Consulter ses bulletins (etudiant).
     */
    @Operation(summary = "Lister les bulletins d'un etudiant")
    @GetMapping("/etudiant/{id}")
    public ResponseEntity<List<ReportCardDTO>> getByStudent(@PathVariable Long id) {
        return ResponseEntity.ok(reportCardService.getReportCardsByStudent(id));
    }

    /**
     * Télécharger le bulletin en PDF.
     */
    @Operation(summary = "Télécharger le PDF", description = "Génère et renvoie le fichier PDF du bulletin")
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) throws IOException {
        ReportCard reportCard = reportCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bulletin introuvable"));

        byte[] pdfContent = pdfService.generateReportCardPdf(reportCard);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bulletin_" + id + ".pdf");
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .headers(headers)
                .body(pdfContent);
    }

    private Long requireLong(Map<String, Object> body, String key) {
        Object raw = body.get(key);
        if (raw == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Champ requis: " + key);
        }
        if (raw instanceof Number) {
            return ((Number) raw).longValue();
        }
        try {
            return Long.valueOf(raw.toString());
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valeur invalide pour: " + key);
        }
    }

    private String requireString(Map<String, Object> body, String key) {
        Object raw = body.get(key);
        if (raw == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Champ requis: " + key);
        }
        String value = raw.toString().trim();
        if (value.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Champ requis: " + key);
        }
        return value;
    }
}
