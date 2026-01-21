package com.example.schoolgestapp.gestions_academique.services;

import com.example.schoolgestapp.entity.*;
import com.example.schoolgestapp.entity.enums.EvaluationType;
import com.example.schoolgestapp.exception.BusinessException;
import com.example.schoolgestapp.gestions_academique.dto.ModuleResultDTO;
import com.example.schoolgestapp.gestions_academique.dto.ReportCardDTO;
import com.example.schoolgestapp.gestions_academique.dto.SubjectResultDTO;
import com.example.schoolgestapp.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Service métier responsable de la génération de bulletins de notes.
 */
@Service
@Transactional
public class ReportCardService {

    private final IReportCard reportCardRepository;
    private final IModuleResult moduleResultRepository;
    private final ISubjectResult subjectResultRepository;
    private final IStudent studentRepository;
    private final ISemester semesterRepository;
    private final IModule moduleRepository;
    private final IGrade gradeRepository;

    public ReportCardService(IReportCard reportCardRepository,
                             IModuleResult moduleResultRepository,
                             ISubjectResult subjectResultRepository,
                             IStudent studentRepository,
                             ISemester semesterRepository,
                             IModule moduleRepository,
                             IGrade gradeRepository) {
        this.reportCardRepository = reportCardRepository;
        this.moduleResultRepository = moduleResultRepository;
        this.subjectResultRepository = subjectResultRepository;
        this.studentRepository = studentRepository;
        this.semesterRepository = semesterRepository;
        this.moduleRepository = moduleRepository;
        this.gradeRepository = gradeRepository;
    }

    public ReportCardDTO generateReportCard(Long studentId, Long semesterId, String academicYear) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException("etudiant introuvable"));
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new BusinessException("Semestre introuvable"));

        String year = academicYear != null ? academicYear : semester.getAcademicYear();
        if (year == null) {
            throw new BusinessException("Année académique non définie pour ce semestre.");
        }

        if (student.getClasse() == null || student.getClasse().getId() == null) {
            throw new BusinessException("Classe introuvable pour l'etudiant");
        }

        reportCardRepository
                .findByStudentAndSemesterAndAcademicYear(student, semester, year)
                .ifPresent(reportCardRepository::delete);

        ReportCard reportCard = new ReportCard();
        reportCard.setStudent(student);
        reportCard.setSemester(semester);
        reportCard.setAcademicYear(year);
        reportCard = reportCardRepository.save(reportCard);

        List<com.example.schoolgestapp.entity.Module> modules = moduleRepository
                .findByClasseId(student.getClasse().getId())
                .stream()
                .filter(m -> m.getSemester() != null && Objects.equals(m.getSemester().getId(), semesterId))
                .collect(Collectors.toList());

        double totalWeightedAverages = 0.0;
        int totalCredits = 0;
        List<ModuleResult> moduleResults = new ArrayList<>();

        for (com.example.schoolgestapp.entity.Module module : modules) {
            ModuleResult mResult = calculateModuleResult(reportCard, module, student);
            moduleResults.add(mResult);
            Integer moduleCredits = resolveModuleCredits(module, mResult);
            if (mResult.getAverage() != null && moduleCredits != null && moduleCredits > 0) {
                totalWeightedAverages += mResult.getAverage() * moduleCredits;
                totalCredits += moduleCredits;
            }
        }

        reportCard.setModuleResults(moduleResults);
        reportCard.setAverage(totalCredits > 0 ? totalWeightedAverages / totalCredits : 0.0);

        return toReportCardDTO(reportCardRepository.save(reportCard));
    }

    private ModuleResult calculateModuleResult(ReportCard reportCard, com.example.schoolgestapp.entity.Module module, Student student) {
        ModuleResult mResult = new ModuleResult();
        mResult.setReportCard(reportCard);
        mResult.setModule(module);
        mResult = moduleResultRepository.save(mResult);

        List<SubjectResult> subjectResults = new ArrayList<>();
        double totalWeightedSubjects = 0.0;
        int moduleCredits = 0;

        List<Subject> subjects = module.getSubjects() != null ? module.getSubjects() : Collections.emptyList();
        for (Subject subject : subjects) {
            if (subject == null) {
                continue;
            }
            SubjectResult sResult = calculateSubjectResult(mResult, subject, student);
            subjectResults.add(sResult);
            if (sResult.getFinalAverage() != null) {
                int credits = subject.getCredits() != null && subject.getCredits() > 0 ? subject.getCredits() : 1;
                totalWeightedSubjects += sResult.getFinalAverage() * credits;
                moduleCredits += credits;
            }
        }

        mResult.setSubjectResults(subjectResults);
        mResult.setTotalCredits(moduleCredits);
        mResult.setAverage(moduleCredits > 0 ? totalWeightedSubjects / moduleCredits : 0.0);
        return moduleResultRepository.save(mResult);
    }

    private SubjectResult calculateSubjectResult(ModuleResult mResult, Subject subject, Student student) {
        List<Grade> grades = gradeRepository.findByStudentAndSubject(student, subject);
        double ccWeightedSum = 0; double ccWeightTotal = 0;
        double examWeightedSum = 0; double examWeightTotal = 0;

        for (Grade g : grades) {
            if (g == null) {
                continue;
            }
            double weight = g.getWeight() != null ? g.getWeight() : 1.0;
            double weightedScore = calculateWeightedScore(g);
            if (g.getEvaluationType() == EvaluationType.EXAMEN_FINAL || g.getEvaluationType() == EvaluationType.EXAMEN_PARTIEL) {
                examWeightedSum += weightedScore;
                examWeightTotal += weight;
            } else {
                ccWeightedSum += weightedScore;
                ccWeightTotal += weight;
            }
        }

        double ccAvg = ccWeightTotal > 0 ? ccWeightedSum / ccWeightTotal : 0.0;
        double examAvg = examWeightTotal > 0 ? examWeightedSum / examWeightTotal : 0.0;
        
        // La moyenne finale de la matière combine CC et Examen selon les coefficients de la matière
        double coeffCc = subject.getCoefficientCC() != null ? subject.getCoefficientCC() : 0.4;
        double coeffExam = subject.getCoefficientExam() != null ? subject.getCoefficientExam() : 0.6;
        double finalAvg = (ccAvg * coeffCc) + (examAvg * coeffExam);

        SubjectResult sResult = new SubjectResult();
        sResult.setModuleResult(mResult);
        sResult.setSubject(subject);
        sResult.setCcAverage(ccAvg);
        sResult.setExamGrade(examAvg);
        sResult.setFinalAverage(finalAvg);
        return subjectResultRepository.save(sResult);
    }

    public void generateAllReportCards(Long semesterId, String academicYear) {
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new BusinessException("Semestre introuvable"));
        
        String year = academicYear != null ? academicYear : semester.getAcademicYear();
        if (year == null) {
            throw new BusinessException("Année académique non définie pour ce semestre.");
        }

        // On identifie tous les étudiants ayant eu au moins une note ce semestre
        Set<Long> studentIds = gradeRepository.findAll().stream()
                .filter(g -> g.getSubject() != null && g.getSubject().getSemester() != null 
                        && Objects.equals(g.getSubject().getSemester().getId(), semesterId))
                .map(g -> g.getStudent().getId())
                .collect(Collectors.toSet());

        for (Long studentId : studentIds) {
            try {
                generateReportCard(studentId, semesterId, year);
            } catch (Exception e) {
                // Log and continue to next student
                System.err.println("Erreur génération bulletin pour étudiant " + studentId + " : " + e.getMessage());
            }
        }
        
        // Enfin on recalcule les rangs
        calculateClassRanks(semesterId, year);
    }

    public void calculateClassRanks(Long semesterId, String academicYear) {
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new BusinessException("Semestre introuvable"));
        
        String year = academicYear != null ? academicYear : semester.getAcademicYear();
        
        List<ReportCard> cards = reportCardRepository.findBySemesterAndAcademicYear(semester, year);
        cards.sort(Comparator.comparing(ReportCard::getAverage, Comparator.nullsFirst(Double::compareTo)).reversed());
        for (int i = 0; i < cards.size(); i++) {
            cards.get(i).setRank((i + 1) + " / " + cards.size());
            reportCardRepository.save(cards.get(i));
        }
    }

    public List<ReportCardDTO> getReportCardsByStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException("etudiant introuvable"));
        return reportCardRepository.findByStudent(student).stream()
                .map(this::toReportCardDTO)
                .collect(Collectors.toList());
    }

    /* ===================== mapping methode ================= */

    private ReportCardDTO toReportCardDTO(ReportCard rc) {
        if (rc == null) return null;
        return ReportCardDTO.builder()
                .id(rc.getId())
                .studentId(rc.getStudent().getId())
                .studentName(rc.getStudent().getUser().getFirstName() + " " + rc.getStudent().getUser().getLastName())
                .semesterId(rc.getSemester().getId())
                .semesterName(rc.getSemester().getName())
                .academicYear(rc.getAcademicYear())
                .average(rc.getAverage())
                .rank(parseRank(rc.getRank()))
                .appreciation(rc.getAppreciation())
                .validated(rc.isValidated())
                .createdAt(rc.getCreatedAt())
                .moduleResults(rc.getModuleResults() != null ? rc.getModuleResults().stream().map(this::toModuleResultDTO).collect(Collectors.toList()) : null)
                .build();
    }

    private ModuleResultDTO toModuleResultDTO(ModuleResult mr) {
        if (mr == null) return null;
        return ModuleResultDTO.builder()
                .id(mr.getId())
                .moduleId(mr.getModule().getId())
                .moduleName(mr.getModule().getName())
                .average(mr.getAverage())
                .totalCredits(mr.getTotalCredits())
                .subjectResults(mr.getSubjectResults() != null ? mr.getSubjectResults().stream().map(this::toSubjectResultDTO).collect(Collectors.toList()) : null)
                .build();
    }

    private SubjectResultDTO toSubjectResultDTO(SubjectResult sr) {
        if (sr == null) return null;
        return SubjectResultDTO.builder()
                .id(sr.getId())
                .subjectId(sr.getSubject().getId())
                .subjectName(sr.getSubject().getName())
                .ccAverage(sr.getCcAverage())
                .examGrade(sr.getExamGrade())
                .finalAverage(sr.getFinalAverage())
                .teacherAppreciation(sr.getTeacherAppreciation())
                .build();
    }

    private Integer resolveModuleCredits(com.example.schoolgestapp.entity.Module module, ModuleResult mResult) {
        if (module != null && module.getCredits() != null && module.getCredits() > 0) {
            return module.getCredits();
        }
        if (mResult != null && mResult.getTotalCredits() != null && mResult.getTotalCredits() > 0) {
            return mResult.getTotalCredits();
        }
        return 0;
    }

    private Integer parseRank(String raw) {
        if (raw == null) return null;
        String value = raw.trim();
        if (value.isEmpty()) return null;
        int slashIndex = value.indexOf('/');
        String left = slashIndex >= 0 ? value.substring(0, slashIndex) : value;
        left = left.replace(" ", "");
        if (left.isEmpty()) return null;
        try {
            return Integer.parseInt(left);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private double calculateWeightedScore(Grade g) {
        Double score = g.getScore();
        if (score == null) return 0.0;
        double maxScore = g.getMaxScore() != null && g.getMaxScore() > 0 ? g.getMaxScore() : 20.0;
        double weight = g.getWeight() != null ? g.getWeight() : 1.0;
        return (score / maxScore) * 20.0 * weight;
    }
}
