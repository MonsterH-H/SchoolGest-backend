package com.example.schoolgestapp.gestions_emploidutemps.services;

import com.example.schoolgestapp.entity.*;
import com.example.schoolgestapp.exception.BusinessException;
import com.example.schoolgestapp.gestions_emploidutemps.dto.PlanningDTO;
import com.example.schoolgestapp.gestions_emploidutemps.dto.SalleDTO;
import com.example.schoolgestapp.gestions_emploidutemps.dto.TimeSlotDTO;
import com.example.schoolgestapp.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SERVICE : Gestion de l'Emploi du Temps et Planification
 * 
 * Rôles :
 * - Configurer les salles et les créneaux horaires
 * - Planifier les cours en évitant les surcharges (Conflits)
 * - Gérer les imprévus (Annulations, Reports de séances)
 * - Fournir les vues filtrées pour les etudiants et enseignants
 */
@Service
@Transactional
public class ScheduleService {

    private final IPlanning planningRepository;
    private final ITimeSlot timeSlotRepository;
    private final ISalle salleRepository;
    private final ISubject subjectRepository;
    private final IClasse classeRepository;
    private final ITeacher teacherRepository;

    public ScheduleService(IPlanning planningRepository, 
                           ITimeSlot timeSlotRepository, 
                           ISalle salleRepository,
                           ISubject subjectRepository,
                           IClasse classeRepository,
                           ITeacher teacherRepository) {
        this.planningRepository = planningRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.salleRepository = salleRepository;
        this.subjectRepository = subjectRepository;
        this.classeRepository = classeRepository;
        this.teacherRepository = teacherRepository;
    }

    /* ===================== CONFIGURATION INFRASTRUCTURE ===================== */

    /** Crée une nouvelle salle (Amphi, salle de TP, etc.). */
    public SalleDTO creerSalle(Salle salle) { return toSalleDTO(salleRepository.save(salle)); }
    
    /** Liste toutes les salles disponibles pour la planification. */
    public List<SalleDTO> listerToutesLesSalles() { 
        return salleRepository.findAll().stream().map(this::toSalleDTO).collect(Collectors.toList()); 
    }
    
    /** Définit un créneau horaire standard (ex: 08:00 - 10:00). */
    public TimeSlotDTO creerTimeSlot(TimeSlot slot) { return toTimeSlotDTO(timeSlotRepository.save(slot)); }
    
    public List<TimeSlotDTO> listerTousLesTimeSlots() { 
        return timeSlotRepository.findAll().stream().map(this::toTimeSlotDTO).collect(Collectors.toList()); 
    }

    public TimeSlotDTO updateTimeSlot(Long id, TimeSlot slot) {
        TimeSlot existing = timeSlotRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Créneau horaire introuvable"));
        existing.setLabel(slot.getLabel());
        existing.setStartTime(slot.getStartTime());
        existing.setEndTime(slot.getEndTime());
        existing.setPause(slot.isPause());
        existing.setActive(slot.isActive());
        return toTimeSlotDTO(timeSlotRepository.save(existing));
    }

    public void deleteTimeSlot(Long id) {
        if (planningRepository.existsByTimeSlotId(id)) {
            throw new BusinessException("Impossible de supprimer ce créneau, il est utilisé dans une planification.");
        }
        timeSlotRepository.deleteById(id);
    }

    /* ===================== GESTION DES SEANCES ===================== */

    /**
     * Planifie un cours à une date et un créneau précis.
     * Cette méthode déclenche SYSTEMATIQUEMENT une vérification de conflit.
     */
    public PlanningDTO planifierCours(PlanningDTO dto) {
        Planning planning = new Planning();
        planning.setDate(dto.getDate());
        planning.setCourseType(dto.getCourseType());

        if (dto.getSubjectId() == null) throw new BusinessException("Matière introuvable");
        if (dto.getClasseId() == null) throw new BusinessException("Classe introuvable");
        if (dto.getTeacherId() == null) throw new BusinessException("Enseignant introuvable");
        if (dto.getTimeSlotId() == null) throw new BusinessException("Créneau horaire introuvable");

        planning.setSubject(subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new BusinessException("Matière introuvable")));
        planning.setClasse(classeRepository.findById(dto.getClasseId())
                .orElseThrow(() -> new BusinessException("Classe introuvable")));
        planning.setTeacher(teacherRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new BusinessException("Enseignant introuvable")));
        planning.setTimeSlot(timeSlotRepository.findById(dto.getTimeSlotId())
                .orElseThrow(() -> new BusinessException("Créneau horaire introuvable")));

        if (dto.getRoomId() != null) {
            planning.setRoom(salleRepository.findById(dto.getRoomId())
                    .orElseThrow(() -> new BusinessException("Salle introuvable")));
        }

        validerConflits(planning);
        return toPlanningDTO(planningRepository.save(planning));
    }

    public PlanningDTO updatePlanning(Long planningId, PlanningDTO dto) {
        Planning existing = planningRepository.findById(planningId)
                .orElseThrow(() -> new BusinessException("Séance introuvable"));

        if (dto.getDate() != null) existing.setDate(dto.getDate());
        if (dto.getCourseType() != null) existing.setCourseType(dto.getCourseType());

        if (dto.getSubjectId() != null) {
            existing.setSubject(subjectRepository.findById(dto.getSubjectId())
                    .orElseThrow(() -> new BusinessException("Matière introuvable")));
        }
        if (dto.getClasseId() != null) {
            existing.setClasse(classeRepository.findById(dto.getClasseId())
                    .orElseThrow(() -> new BusinessException("Classe introuvable")));
        }
        if (dto.getTeacherId() != null) {
            existing.setTeacher(teacherRepository.findById(dto.getTeacherId())
                    .orElseThrow(() -> new BusinessException("Enseignant introuvable")));
        }
        if (dto.getTimeSlotId() != null) {
            existing.setTimeSlot(timeSlotRepository.findById(dto.getTimeSlotId())
                    .orElseThrow(() -> new BusinessException("Créneau horaire introuvable")));
        }

        if (dto.getRoomId() != null) {
            existing.setRoom(salleRepository.findById(dto.getRoomId())
                    .orElseThrow(() -> new BusinessException("Salle introuvable")));
        }

        validerConflits(existing);
        return toPlanningDTO(planningRepository.save(existing));
    }

    public void deletePlanning(Long planningId) {
        Planning existing = planningRepository.findById(planningId)
                .orElseThrow(() -> new BusinessException("Séance introuvable"));
        planningRepository.delete(existing);
    }

    /**
     * LOGIQUE ANTI-CONFLIT (Optimisée SQL)
     * Vérifie 3 règles d'or :
     * 1. L'enseignant n'est pas déjà en train de donner un cours ailleurs.
     * 2. La salle n'est pas déjà occupée par une autre classe.
     * 3. La classe n'a pas déjà un autre cours prévu au même moment.
     */
    public void validerConflits(Planning p) {
        List<Planning> existants = planningRepository.findActiveByDateAndTimeSlot(p.getDate(), p.getTimeSlot());

        for (Planning e : existants) {
            // Ignorer si c'est la même séance qu'on est en train de modifier
            if (p.getId() != null && p.getId().equals(e.getId())) continue;

            if (e.getTeacher().getId().equals(p.getTeacher().getId())) {
                throw new BusinessException("L'enseignant " + e.getTeacher().getUser().getLastName() + " est déjà pris.");
            }
            if (e.getRoom() != null && p.getRoom() != null && e.getRoom().getId().equals(p.getRoom().getId())) {
                throw new BusinessException("La salle " + e.getRoom().getNom() + " est occupée.");
            }
            if (e.getClasse().getId().equals(p.getClasse().getId())) {
                throw new BusinessException("La classe " + e.getClasse().getName() + " a déjà un cours.");
            }
        }
    }

    /* ===================== CONSULTATION ===================== */

    /** Récupère l'emploi du temps d'une classe pour une période donnée. */
    public List<PlanningDTO> getPlanningParClasse(Long classeId, LocalDate debut, LocalDate fin) {
        return planningRepository.findByClasseIdAndDateBetween(classeId, debut, fin).stream()
                .map(this::toPlanningDTO)
                .collect(Collectors.toList());
    }

    /** Récupère le planning de travail d'un enseignant. */
    public List<PlanningDTO> getPlanningParEnseignant(Long teacherId, LocalDate debut, LocalDate fin) {
        return planningRepository.findByTeacherIdAndDateBetween(teacherId, debut, fin).stream()
                .map(this::toPlanningDTO)
                .collect(Collectors.toList());
    }

    /* ===================== MODIFICATIONS D'URGENCE ===================== */

    /**
     * Annule une séance. La séance reste en base mais marquée "Cancelled" pour historique.
     */
    public PlanningDTO annulerSeance(Long planningId, String motif) {
        Planning p = planningRepository.findById(planningId)
                .orElseThrow(() -> new BusinessException("Séance introuvable"));
        p.setCancelled(true);
        p.setCancellationReason(motif);
        return toPlanningDTO(planningRepository.save(p));
    }

    /**
     * Reporte une séance de cours. 
     * L'ancienne séance est marquée annulée et une nouvelle est créée (avec vérification de conflit).
     */
    public PlanningDTO reporterSeance(Long planningId, LocalDate nouvelleDate, Long nouveauTimeSlotId) {
        Planning original = planningRepository.findById(planningId)
                .orElseThrow(() -> new BusinessException("Séance d'origine introuvable"));
        
        TimeSlot nouveauCreneau = timeSlotRepository.findById(nouveauTimeSlotId)
                .orElseThrow(() -> new BusinessException("Créneau horaire introuvable"));

        // Marquer l'originale comme annulée
        original.setCancelled(true);
        original.setCancellationReason("Reporté au " + nouvelleDate);
        planningRepository.save(original);

        // Créer la nouvelle séance clone
        Planning nouvelleSession = new Planning();
        nouvelleSession.setSubject(original.getSubject());
        nouvelleSession.setClasse(original.getClasse());
        nouvelleSession.setTeacher(original.getTeacher());
        nouvelleSession.setRoom(original.getRoom());
        nouvelleSession.setDate(nouvelleDate);
        nouvelleSession.setTimeSlot(nouveauCreneau);
        nouvelleSession.setCourseType(original.getCourseType());
        nouvelleSession.setRescheduled(true);
        nouvelleSession.setOriginalPlanningId(original.getId());

        validerConflits(nouvelleSession); 
        return toPlanningDTO(planningRepository.save(nouvelleSession));
    }

    /* ===================== mapping methode ================= */

    private PlanningDTO toPlanningDTO(Planning p) {
        if (p == null) return null;
        return PlanningDTO.builder()
                .id(p.getId())
                .subjectId(p.getSubject() != null ? p.getSubject().getId() : null)
                .subjectName(p.getSubject() != null ? p.getSubject().getName() : null)
                .teacherId(p.getTeacher() != null ? p.getTeacher().getId() : null)
                .teacherName(p.getTeacher() != null ? p.getTeacher().getUser().getFirstName() + " " + p.getTeacher().getUser().getLastName() : null)
                .classeId(p.getClasse() != null ? p.getClasse().getId() : null)
                .classeName(p.getClasse() != null ? p.getClasse().getName() : null)
                .roomId(p.getRoom() != null ? p.getRoom().getId() : null)
                .roomName(p.getRoom() != null ? p.getRoom().getNom() : null)
                .date(p.getDate())
                .timeSlotId(p.getTimeSlot() != null ? p.getTimeSlot().getId() : null)
                .timeSlotLabel(p.getTimeSlot() != null ? p.getTimeSlot().getStartTime() + " - " + p.getTimeSlot().getEndTime() : null)
                .courseType(p.getCourseType())
                .cancelled(p.isCancelled())
                .cancellationReason(p.getCancellationReason())
                .rescheduled(p.isRescheduled())
                .originalPlanningId(p.getOriginalPlanningId())
                .build();
    }

    private SalleDTO toSalleDTO(Salle s) {
        if (s == null) return null;
        return SalleDTO.builder()
                .id(s.getId())
                .nom(s.getNom())
                .capacite(s.getCapacite())
                .type(s.getType())
                .build();
    }

    private TimeSlotDTO toTimeSlotDTO(TimeSlot ts) {
        if (ts == null) return null;
        return TimeSlotDTO.builder()
                .id(ts.getId())
                .startTime(ts.getStartTime())
                .endTime(ts.getEndTime())
                .label(ts.getLabel())
                .build();
    }
}
