package com.example.schoolgestapp.gestions_cahier_texte.services;

import com.example.schoolgestapp.entity.*;
import com.example.schoolgestapp.exception.BusinessException;
import com.example.schoolgestapp.gestions_cahier_texte.dto.SeanceCreateDTO;
import com.example.schoolgestapp.gestions_cahier_texte.dto.SeanceResponseDTO;
import com.example.schoolgestapp.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion du Cahier de Texte Numérique.
 */
@Service
@Transactional
public class CahierDeTexteService {

    private final ICahierDeTexte cahierDeTexteRepository;
    private final ISeancePedagogique seancePedagogiqueRepository;
    private final IClasse classeRepository;
    private final ITeacher teacherRepository;
    private final ISubject subjectRepository;
    private final IPlanning planningRepository;
    private final IAssignment assignmentRepository;

    public CahierDeTexteService(ICahierDeTexte cahierDeTexteRepository,
                                 ISeancePedagogique seancePedagogiqueRepository,
                                 IClasse classeRepository,
                                 ITeacher teacherRepository,
                                 ISubject subjectRepository,
                                 IPlanning planningRepository,
                                 IAssignment assignmentRepository) {
        this.cahierDeTexteRepository = cahierDeTexteRepository;
        this.seancePedagogiqueRepository = seancePedagogiqueRepository;
        this.classeRepository = classeRepository;
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
        this.planningRepository = planningRepository;
        this.assignmentRepository = assignmentRepository;
    }

    /**
     * Récupère ou crée le cahier de texte d'une classe.
     * Règle n°1 : Le cahier appartient à l'établissement/classe.
     */
    public CahierDeTexte getOrCreateCahier(Long classeId) {
        return cahierDeTexteRepository.findByClasseId(classeId)
                .orElseGet(() -> createAndSaveCahierDeTexte(classeId));
    }

    private CahierDeTexte createAndSaveCahierDeTexte(Long classeId) {
        Classe classe = classeRepository.findById(classeId)
                .orElseThrow(() -> new BusinessException("Classe non trouvée"));
        CahierDeTexte cdt = new CahierDeTexte();
        cdt.setClasse(classe);
        cdt.setArchive(false);
        return cahierDeTexteRepository.save(cdt);
    }

    /**
     * Ajoute une séance pédagogique (Enseignant contributeur).
     */
    public SeanceResponseDTO createSeance(SeanceCreateDTO dto) {
        SeancePedagogique seance = new SeancePedagogique();

        // Utiliser getOrCreateCahier pour récupérer ou créer le cahier de texte
        CahierDeTexte cahierDeTexte = getOrCreateCahier(dto.getCahierDeTexteId());
        seance.setCahierDeTexte(cahierDeTexte);
        
        seance.setEnseignant(teacherRepository.findById(dto.getEnseignantId())
                .orElseThrow(() -> new BusinessException("Enseignant non trouvé")));
        
        seance.setMatiere(subjectRepository.findById(dto.getMatiereId())
                .orElseThrow(() -> new BusinessException("Matière non trouvée")));

        if (dto.getPlanningId() != null) {
            seance.setPlanning(planningRepository.findById(dto.getPlanningId()).orElse(null));
        }
        
        if (dto.getAssignmentId() != null) {
            seance.setAssignment(assignmentRepository.findById(dto.getAssignmentId()).orElse(null));
        }

        seance.setDate(dto.getDate());
        seance.setHeureDebut(dto.getHeureDebut());
        seance.setHeureFin(dto.getHeureFin());
        seance.setObjectifs(dto.getObjectifs());
        seance.setContenuCours(dto.getContenuCours());
        seance.setDevoirs(dto.getDevoirs());
        seance.setDateLimiteDevoir(dto.getDateLimiteDevoir());
        seance.setFichierCloudUrl(dto.getFichierCloudUrl());
        seance.setObservations(dto.getObservations());

        return toResponseDTO(seancePedagogiqueRepository.save(seance));
    }

    /**
     * Récupère toutes les séances d'un cahier de texte (Consultation par classe).
     */
    public List<SeanceResponseDTO> getSeancesByCahier(Long cahierId) {
        return seancePedagogiqueRepository.findByCahierDeTexteId(cahierId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les séances d'une classe pour une date donnée.
     */
    public List<SeanceResponseDTO> getSeancesByClasseAndDate(Long classeId, LocalDate date) {
        CahierDeTexte cdt = getOrCreateCahier(classeId);
        return seancePedagogiqueRepository.findByCahierDeTexteIdAndDate(cdt.getId(), date)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Met à jour une séance. Seul l'auteur peut modifier ses propres séances.
     */
    public SeanceResponseDTO updateSeance(Long seanceId, SeanceCreateDTO dto, Long currentTeacherId) {
        SeancePedagogique existing = seancePedagogiqueRepository.findById(seanceId)
                .orElseThrow(() -> new BusinessException("Séance non trouvée"));

        if (!existing.getEnseignant().getId().equals(currentTeacherId)) {
            throw new BusinessException("Sécurité : Un enseignant ne peut modifier que ses propres séances.");
        }

        // Mise à jour des champs autorisés (Contenu pédagogique et Devoirs)
        existing.setContenuCours(dto.getContenuCours());
        existing.setObjectifs(dto.getObjectifs());
        existing.setDevoirs(dto.getDevoirs());
        existing.setDateLimiteDevoir(dto.getDateLimiteDevoir());
        existing.setFichierCloudUrl(dto.getFichierCloudUrl());
        existing.setObservations(dto.getObservations());
        
        return toResponseDTO(seancePedagogiqueRepository.save(existing));
    }

    /**
     * Archive un cahier de texte (Action Admin).
     */
    public void archiverCahier(Long cahierId) {
        CahierDeTexte cdt = cahierDeTexteRepository.findById(cahierId)
                .orElseThrow(() -> new BusinessException("Cahier de texte non trouvé"));
        cdt.setArchive(true);
        cahierDeTexteRepository.save(cdt);
    }

    /**
     * Récupère les séances d'un enseignant.
     */
    public List<SeanceResponseDTO> getSeancesByTeacher(Long teacherId) {
        return seancePedagogiqueRepository.findByEnseignantId(teacherId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Mapper interne : Entité -> ResponseDTO
     */
    private SeanceResponseDTO toResponseDTO(SeancePedagogique s) {
        SeanceResponseDTO.SeanceResponseDTOBuilder builder = SeanceResponseDTO.builder()
                .id(s.getId())
                .date(s.getDate())
                .heureDebut(s.getHeureDebut().toString())
                .heureFin(s.getHeureFin().toString())
                .matiereId(s.getMatiere().getId())
                .matiereNom(s.getMatiere().getName())
                .matiereCode(s.getMatiere().getCode())
                .enseignantId(s.getEnseignant().getId())
                .enseignantNomComplet(s.getEnseignant().getUser().getFirstName() + " " + s.getEnseignant().getUser().getLastName())
                .objectifs(s.getObjectifs())
                .contenuCours(s.getContenuCours())
                .devoirs(s.getDevoirs())
                .dateLimiteDevoir(s.getDateLimiteDevoir())
                .fichierCloudUrl(s.getFichierCloudUrl())
                .observations(s.getObservations())
                .title(s.getObjectifs()) // Mapping pour compatibilité
                .description(s.getContenuCours()) // Mapping pour compatibilité
                .classeName(s.getCahierDeTexte().getClasse() != null ? s.getCahierDeTexte().getClasse().getName() : "N/A")
                .startTime(s.getHeureDebut().toString())
                .endTime(s.getHeureFin().toString());

        if (s.getPlanning() != null) {
            builder.planningId(s.getPlanning().getId());
        }
        if (s.getAssignment() != null) {
            builder.assignmentId(s.getAssignment().getId());
        }

        return builder.build();
    }
}
