package com.example.schoolgestapp.gestions_academique.services;

import com.example.schoolgestapp.entity.*;
import com.example.schoolgestapp.entity.enums.StudentStatus;
import com.example.schoolgestapp.gestions_academique.dto.*;
import com.example.schoolgestapp.exception.BusinessException;
import com.example.schoolgestapp.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SERVICE : Structure Académique et Inscriptions
 * 
 * Ce service gère les fondations du système scolaire :
 * - Gestion des établissements (campus)
 * - Création de la hiérarchie Classes -> Modules -> Matières
 * - Suivi des inscriptions (Enrollments) des étudiants sur plusieurs années.
 */
@Service
@Transactional
public class AcademicService {

    private final IEstablishment establishmentRepository;
    private final IClasse classeRepository;
    private final ISubject subjectRepository;
    private final ISemester semesterRepository;
    private final IStudent studentRepository;
    private final IModule moduleRepository;
    private final IEnrollment enrollmentRepository;
    private final ITeacher teacherRepository;

    public AcademicService(IEstablishment establishmentRepository, 
                           IClasse classeRepository, 
                           ISubject subjectRepository, 
                           ISemester semesterRepository,
                           IStudent studentRepository,
                           IModule moduleRepository,
                           IEnrollment enrollmentRepository,
                           ITeacher teacherRepository) {
        this.establishmentRepository = establishmentRepository;
        this.classeRepository = classeRepository;
        this.subjectRepository = subjectRepository;
        this.semesterRepository = semesterRepository;
        this.studentRepository = studentRepository;
        this.moduleRepository = moduleRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.teacherRepository = teacherRepository;
    }

    /* ===================== GESTION STRUCTURE ===================== */

    /** Enregistre un nouvel établissement (Campus). */
    public EstablishmentDTO saveEstablishment(Establishment e) { 
        return toEstablishmentDTO(establishmentRepository.save(e)); 
    }

    public EstablishmentDTO updateEstablishment(Long id, Establishment updated) {
        Establishment existing = establishmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Établissement introuvable"));
        if (updated.getName() != null) existing.setName(updated.getName());
        if (updated.getCode() != null) existing.setCode(updated.getCode());
        if (updated.getAddress() != null) existing.setAddress(updated.getAddress());
        if (updated.getPhone() != null) existing.setPhone(updated.getPhone());
        if (updated.getEmail() != null) existing.setEmail(updated.getEmail());
        if (updated.getWebsite() != null) existing.setWebsite(updated.getWebsite());
        if (updated.getLogoUrl() != null) existing.setLogoUrl(updated.getLogoUrl());
        if (updated.getAcademicParameters() != null) existing.setAcademicParameters(updated.getAcademicParameters());
        if (updated.getAcademicYears() != null) existing.setAcademicYears(updated.getAcademicYears());
        return toEstablishmentDTO(establishmentRepository.save(existing));
    }
    
    public void deleteEstablishment(Long id) {
        if (!establishmentRepository.existsById(id)) {
            throw new BusinessException("Établissement introuvable");
        }
        establishmentRepository.deleteById(id);
    }
    
    public List<EstablishmentDTO> getAllEstablishments() { 
        return establishmentRepository.findAll().stream().map(this::toEstablishmentDTO).collect(Collectors.toList()); 
    }

    /** Gère les Modules (UE) qui regroupent plusieurs matières. */
    public ModuleDTO saveModule(com.example.schoolgestapp.entity.Module m) { 
        return toModuleDTO(moduleRepository.save(m)); 
    }
    
    public List<ModuleDTO> getAllModules() { 
        return moduleRepository.findAll().stream().map(this::toModuleDTO).collect(Collectors.toList()); 
    }

    public List<ModuleDTO> getModulesByClasse(Long classeId) {
        return moduleRepository.findByClasseId(classeId).stream().map(this::toModuleDTO).collect(Collectors.toList());
    }

    public ModuleDTO updateModule(Long id, ModuleDTO dto) {
        com.example.schoolgestapp.entity.Module existing = moduleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Module introuvable"));
        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getCredits() != null) existing.setCredits(dto.getCredits());
        if (dto.getClasseId() != null) {
            existing.setClasse(classeRepository.findById(dto.getClasseId())
                    .orElseThrow(() -> new BusinessException("Classe introuvable")));
        }
        if (dto.getSemesterId() != null) {
            existing.setSemester(semesterRepository.findById(dto.getSemesterId())
                    .orElseThrow(() -> new BusinessException("Semestre introuvable")));
        }
        return toModuleDTO(moduleRepository.save(existing));
    }

    public void deleteModule(Long id) {
        if (!moduleRepository.existsById(id)) {
            throw new BusinessException("Module introuvable");
        }
        moduleRepository.deleteById(id);
    }
    
    /** Récupère uniquement les modules associés à une classe donnée. */
    public List<com.example.schoolgestapp.entity.Module> getRawModulesByClasse(Long classeId) {
        return moduleRepository.findByClasseId(classeId);
    }

    /** Gère les Matières (EC) individuelles. */
    public SubjectDTO saveSubject(Subject s) { 
        return toSubjectDTO(subjectRepository.save(s)); 
    }
    
    public List<SubjectDTO> getAllSubjects() { 
        return subjectRepository.findAll().stream().map(this::toSubjectDTO).collect(Collectors.toList()); 
    }

    public List<SubjectDTO> getSubjectsByClasse(Long classeId) {
        return subjectRepository.findByModule_Classe_Id(classeId).stream().map(this::toSubjectDTO).collect(Collectors.toList());
    }

    public List<SubjectDTO> getSubjectsByModule(Long moduleId) {
        return subjectRepository.findByModule_Id(moduleId).stream().map(this::toSubjectDTO).collect(Collectors.toList());
    }

    public SubjectDTO updateSubject(Long id, SubjectDTO dto) {
        Subject existing = subjectRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Matière introuvable"));
        if (dto.getCode() != null) existing.setCode(dto.getCode());
        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getHoursCM() != null) existing.setHoursCM(dto.getHoursCM());
        if (dto.getHoursTD() != null) existing.setHoursTD(dto.getHoursTD());
        if (dto.getHoursTP() != null) existing.setHoursTP(dto.getHoursTP());
        if (dto.getCredits() != null) existing.setCredits(dto.getCredits());
        if (dto.getCoefficientCC() != null) existing.setCoefficientCC(dto.getCoefficientCC());
        if (dto.getCoefficientExam() != null) existing.setCoefficientExam(dto.getCoefficientExam());
        if (dto.getModuleId() != null) {
            existing.setModule(moduleRepository.findById(dto.getModuleId())
                    .orElseThrow(() -> new BusinessException("Module introuvable")));
        }
        if (dto.getTeacherId() != null) {
            existing.setTeacher(teacherRepository.findById(dto.getTeacherId())
                    .orElseThrow(() -> new BusinessException("Enseignant introuvable")));
        }
        if (dto.getSemesterId() != null) {
            existing.setSemester(semesterRepository.findById(dto.getSemesterId())
                    .orElseThrow(() -> new BusinessException("Semestre introuvable")));
        }
        return toSubjectDTO(subjectRepository.save(existing));
    }

    public SubjectDTO assignSubjectToModule(Long subjectId, Long moduleId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new BusinessException("Matière introuvable"));
        com.example.schoolgestapp.entity.Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new BusinessException("Module introuvable"));
        subject.setModule(module);
        return toSubjectDTO(subjectRepository.save(subject));
    }

    public SubjectDTO unassignSubjectFromModule(Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new BusinessException("Matière introuvable"));
        subject.setModule(null);
        return toSubjectDTO(subjectRepository.save(subject));
    }

    public void deleteSubject(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new BusinessException("Matière introuvable");
        }
        subjectRepository.deleteById(id);
    }

    /** Enregistre une classe (Ex: "L3 Informatique"). */
    public ClasseDTO saveClasse(Classe c) { 
        return toClasseDTO(classeRepository.save(c)); 
    }
    
    public List<ClasseDTO> getAllClasses() { 
        return classeRepository.findAll().stream().map(this::toClasseDTO).collect(Collectors.toList()); 
    }

    public ClasseDTO updateClasse(Long id, Classe updated) {
        Classe existing = classeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Classe introuvable"));
        if (updated.getName() != null) existing.setName(updated.getName());
        if (updated.getCode() != null) existing.setCode(updated.getCode());
        if (updated.getAcademicYear() != null) existing.setAcademicYear(updated.getAcademicYear());
        if (updated.getType() != null) existing.setType(updated.getType());
        if (updated.getMaxCapacity() != 0) existing.setMaxCapacity(updated.getMaxCapacity());
        if (updated.getParentClasse() != null) existing.setParentClasse(updated.getParentClasse());
        if (updated.getResponsible() != null) existing.setResponsible(updated.getResponsible());
        if (updated.getDepartment() != null) existing.setDepartment(updated.getDepartment());
        return toClasseDTO(classeRepository.save(existing));
    }

    public void deleteClasse(Long id) {
        if (!classeRepository.existsById(id)) {
            throw new BusinessException("Classe introuvable");
        }
        classeRepository.deleteById(id);
    }
    
    /** Enregistre un semestre. */
    public Semester saveSemester(Semester s) { return semesterRepository.save(s); }
    
    public List<Semester> getAllSemesters() {
        return semesterRepository.findAll();
    }

    public Semester updateSemester(Long id, Semester updated) {
        Semester existing = semesterRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Semestre introuvable"));
        if (updated.getName() != null) existing.setName(updated.getName());
        if (updated.getAcademicYear() != null) existing.setAcademicYear(updated.getAcademicYear());
        if (updated.getStartDate() != null) existing.setStartDate(updated.getStartDate());
        if (updated.getEndDate() != null) existing.setEndDate(updated.getEndDate());
        if (updated.getExamStartDate() != null) existing.setExamStartDate(updated.getExamStartDate());
        if (updated.getExamEndDate() != null) existing.setExamEndDate(updated.getExamEndDate());
        if (updated.getVacationStartDate() != null) existing.setVacationStartDate(updated.getVacationStartDate());
        if (updated.getVacationEndDate() != null) existing.setVacationEndDate(updated.getVacationEndDate());
        existing.setActive(updated.isActive());
        return semesterRepository.save(existing);
    }

    public void deleteSemester(Long id) {
        if (!semesterRepository.existsById(id)) {
            throw new BusinessException("Semestre introuvable");
        }
        semesterRepository.deleteById(id);
    }
    
    /**
     * Crée un sous-groupe (ex: "Groupe TP 1") rattaché à une classe parente.
     * Hérite automatiquement de l'année académique et du département du parent.
     */
    public ClasseDTO createGroup(Long parentClasseId, String name, String type) {
        Classe parent = classeRepository.findById(parentClasseId)
                .orElseThrow(() -> new BusinessException("Classe parente non trouvée"));

        Classe group = new Classe();
        group.setName(name);
        group.setType(type);
        group.setParentClasse(parent);
        group.setAcademicYear(parent.getAcademicYear());
        group.setDepartment(parent.getDepartment());
        return toClasseDTO(classeRepository.save(group));
    }

    /* ===================== GESTION INSCRIPTIONS ===================== */
    
    /**
     * Inscrit un etudiant dans une classe pour une année scolaire précise.
     * Cette méthode crée un enregistrement d'historique (Enrollment) qui permet
     * de garder la trace du parcours de l'élève au fil des ans.
     */
    public Enrollment enrollStudent(Long studentId, Long classeId, String year) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException("etudiant non trouvé"));
        Classe classe = classeRepository.findById(classeId)
                .orElseThrow(() -> new BusinessException("Classe non trouvée"));

        // Mise à jour de la classe actuelle de l'etudiant
        student.setClasse(classe);
        studentRepository.save(student);

        // Création de la trace d'historique d'inscriptions
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setClasse(classe);
        enrollment.setAcademicYear(year);
        enrollment.setStatus(StudentStatus.INSCRIT);
        
        return enrollmentRepository.save(enrollment);
    }

    /** Permet une inscription rapide en déduisant automatiquement l'année en cours (ex: "2024-2025"). */
    public Enrollment enrollStudent(Long studentId, Long classeId) {
        String currentYear = Year.now().getValue() + "-" + (Year.now().getValue() + 1);
        return enrollStudent(studentId, classeId, currentYear);
    }

    /**
     * Annule l'inscription d'un étudiant pour l'année en cours.
     * Le statut de l'inscription (Enrollment) est mis à jour pour conserver l'historique,
     * au lieu d'être supprimé. La classe actuelle de l'étudiant est également retirée.
     */
    public void unenrollStudent(Long studentId, Long classeId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException("Étudiant introuvable avec l'ID : " + studentId));
        Classe classe = classeRepository.findById(classeId)
                .orElseThrow(() -> new BusinessException("Classe introuvable avec l'ID : " + classeId));

        Enrollment enrollment = enrollmentRepository.findByStudentAndClasse(student, classe)
                .orElseThrow(() -> new BusinessException("L'étudiant n'est pas inscrit dans cette classe."));

        // On ne supprime pas l'enregistrement, on change son statut pour garder l'historique.
        enrollment.setStatus(StudentStatus.ANNULE); // Assumes ANNULE status exists
        enrollmentRepository.save(enrollment);

        // On retire l'affectation de l'étudiant à la classe.
        student.setClasse(null);
        studentRepository.save(student);
    }

    public List<com.example.schoolgestapp.gestions_users.dto.StudentDTO> getStudentsByClasse(Long classeId) {
        return studentRepository.findByClasseId(classeId).stream()
                .map(this::toStudentDTO)
                .collect(Collectors.toList());
    }

    /* ===================== mapping methode ================= */
    
    private EstablishmentDTO toEstablishmentDTO(Establishment e) {
        if (e == null) return null;

        EstablishmentDTO.EstablishmentDTOBuilder builder = EstablishmentDTO.builder()
                .id(e.getId())
                .name(e.getName())
                .code(e.getCode())
                .address(e.getAddress())
                .phone(e.getPhone())
                .email(e.getEmail())
                .website(e.getWebsite())
                .logoUrl(e.getLogoUrl())
                .academicYears(e.getAcademicYears())
                .academicParameters(e.getAcademicParameters());

        return builder.build();
    }

    private ClasseDTO toClasseDTO(Classe c) {
        if (c == null) return null;
        ClasseDTO.ClasseDTOBuilder builder = ClasseDTO.builder()
                .id(c.getId())
                .name(c.getName())
                .code(c.getCode())
                .academicYear(c.getAcademicYear())
                .maxCapacity(c.getMaxCapacity())
                .type(c.getType());

        if (c.getParentClasse() != null) {
            builder.parentClasseId(c.getParentClasse().getId());
            builder.parentClasseName(c.getParentClasse().getName());
        }

        if (c.getResponsible() != null) {
            builder.responsibleId(c.getResponsible().getId());
            builder.responsibleName(c.getResponsible().getUser().getFirstName() + " " + c.getResponsible().getUser().getLastName());
        }

        if (c.getDepartment() != null) {
            builder.departmentId(c.getDepartment().getId());
            builder.departmentName(c.getDepartment().getName());
        }

        return builder.build();
    }

    private SubjectDTO toSubjectDTO(Subject s) {
        if (s == null) return null;
        SubjectDTO.SubjectDTOBuilder builder = SubjectDTO.builder()
                .id(s.getId())
                .code(s.getCode())
                .name(s.getName())
                .hoursCM(s.getHoursCM())
                .hoursTD(s.getHoursTD())
                .hoursTP(s.getHoursTP())
                .credits(s.getCredits())
                .coefficientCC(s.getCoefficientCC())
                .coefficientExam(s.getCoefficientExam());

        if (s.getModule() != null) {
            builder.moduleId(s.getModule().getId());
            builder.moduleName(s.getModule().getName());
        }

        if (s.getTeacher() != null) {
            builder.teacherId(s.getTeacher().getId());
            builder.teacherName(s.getTeacher().getUser().getFirstName() + " " + s.getTeacher().getUser().getLastName());
        }

        if (s.getSemester() != null) {
            builder.semesterId(s.getSemester().getId());
            builder.semesterName(s.getSemester().getName());
        }

        return builder.build();
    }

    private ModuleDTO toModuleDTO(com.example.schoolgestapp.entity.Module m) {
        if (m == null) return null;
        
        ModuleDTO.ModuleDTOBuilder builder = ModuleDTO.builder()
                .id(m.getId())
                .name(m.getName())
                .credits(m.getCredits());

        if (m.getClasse() != null) {
            builder.classeId(m.getClasse().getId());
            builder.classeName(m.getClasse().getName());
        }

        if (m.getSemester() != null) {
            builder.semesterId(m.getSemester().getId());
            builder.semesterName(m.getSemester().getName());
        }

        if (m.getSubjects() != null) {
            builder.subjects(m.getSubjects().stream().map(this::toSubjectDTO).collect(Collectors.toList()));
        }

        return builder.build();
    }

    private com.example.schoolgestapp.gestions_users.dto.StudentDTO toStudentDTO(Student s) {
        if (s == null) return null;
        
        return com.example.schoolgestapp.gestions_users.dto.StudentDTO.builder()
                .id(s.getId())
                .userId(s.getUser().getId())
                .name(s.getUser().getFirstName() + " " + s.getUser().getLastName())
                .email(s.getUser().getEmail())
                .phone(s.getUser().getPhone())
                .studentNumber(s.getStudentCode())
                .birthDate(s.getBirthDate())
                .status(s.getStatus())
                .classeId(s.getClasse() != null ? s.getClasse().getId() : null)
                .classeName(s.getClasse() != null ? s.getClasse().getName() : null)
                .build();
    }
}
