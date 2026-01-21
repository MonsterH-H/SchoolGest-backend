package com.example.schoolgestapp.gestions_users.controller;

import com.example.schoolgestapp.entity.Student;
import com.example.schoolgestapp.entity.Teacher;
import com.example.schoolgestapp.entity.User;
import com.example.schoolgestapp.gestions_ressources.services.FileUploadService;
import com.example.schoolgestapp.gestions_users.dto.StudentDTO;
import com.example.schoolgestapp.gestions_users.dto.TeacherDTO;
import com.example.schoolgestapp.repository.IStudent;
import com.example.schoolgestapp.repository.ITeacher;
import com.example.schoolgestapp.repository.IUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * CONTROLEUR : Gestion approfondie des profils (etudiants / Enseignants).
 */
@RestController
@RequestMapping("/api/profils")
@Tag(name = "02. Profils & Espace Personnel", description = "Mise à jour des informations spécifiques (CV pour professeurs, Bio pour etudiants, etc.)")
public class UserProfileController {

    private final IStudent studentRepository;
    private final ITeacher teacherRepository;
    private final IUser userRepository;
    private final FileUploadService fileUploadService;
    private final ObjectMapper objectMapper;

    public UserProfileController(IStudent studentRepository, 
                                 ITeacher teacherRepository, 
                                 IUser userRepository,
                                 FileUploadService fileUploadService, 
                                 ObjectMapper objectMapper) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.userRepository = userRepository;
        this.fileUploadService = fileUploadService;
        this.objectMapper = objectMapper;
    }

    /**
     * Mise à jour spécifique du profil ENSEIGNANT (avec CV).
     */
    @PutMapping(value = "/enseignant", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TeacherDTO> updateTeacherProfile(
            @RequestPart("data") String dataJson,
            @RequestPart(value = "cv", required = false) MultipartFile cvFile) throws Exception {
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        Teacher teacher = teacherRepository.findByUser(user).orElseThrow();

        Teacher updatedData = objectMapper.readValue(dataJson, Teacher.class);
        
        // Mise à jour des champs
        if (updatedData.getSpecialties() != null) teacher.setSpecialties(updatedData.getSpecialties());
        if (updatedData.getOffice() != null) teacher.setOffice(updatedData.getOffice());
        if (updatedData.getDepartment() != null) teacher.setDepartment(updatedData.getDepartment());

        // Gestion du CV (PDF/Doc)
        if (cvFile != null && !cvFile.isEmpty()) {
            String cvUrl = fileUploadService.uploadFile(cvFile, "cv_enseignants");
            teacher.setCvUrl(cvUrl);
        }

        return ResponseEntity.ok(toTeacherDTO(teacherRepository.save(teacher)));
    }

    /**
     * Mise à jour spécifique du profil etudiant.
     */
    @PutMapping(value = "/etudiant", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StudentDTO> updateStudentProfile(
            @RequestPart("data") String dataJson,
            @RequestPart(value = "photo", required = false) MultipartFile photo) throws Exception {
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        Student student = studentRepository.findByUser(user).orElseThrow();

        Student updatedData = objectMapper.readValue(dataJson, Student.class);
        
        if (updatedData.getBio() != null) student.setBio(updatedData.getBio());
        if (updatedData.getBirthDate() != null) student.setBirthDate(updatedData.getBirthDate());
        if (updatedData.getNationality() != null) student.setNationality(updatedData.getNationality());

        return ResponseEntity.ok(toStudentDTO(studentRepository.save(student)));
    }

    private TeacherDTO toTeacherDTO(Teacher t) {
        if (t == null) return null;
        return TeacherDTO.builder()
                .id(t.getId())
                .userId(t.getUser().getId())
                .name(t.getUser().getFirstName() + " " + t.getUser().getLastName())
                .email(t.getUser().getEmail())
                .phone(t.getUser().getPhone())
                .specialties(t.getSpecialties())
                .office(t.getOffice())
                .departmentName(t.getDepartment() != null ? t.getDepartment().getName() : null)
                .cvUrl(t.getCvUrl())
                .hireDate(t.getHireDate())
                .teacherCode(t.getTeacherCode())
                .build();
    }

    private StudentDTO toStudentDTO(Student s) {
        if (s == null) return null;
        return StudentDTO.builder()
                .id(s.getId())
                .userId(s.getUser().getId())
                .name(s.getUser().getFirstName() + " " + s.getUser().getLastName())
                .email(s.getUser().getEmail())
                .phone(s.getUser().getPhone())
                .classeId(s.getClasse() != null ? s.getClasse().getId() : null)
                .classeName(s.getClasse() != null ? s.getClasse().getName() : null)
                .studentNumber(s.getStudentCode())
                .birthDate(s.getBirthDate())
                .bio(s.getBio())
                .nationality(s.getNationality())
                .status(s.getStatus())
                .build();
    }
}
