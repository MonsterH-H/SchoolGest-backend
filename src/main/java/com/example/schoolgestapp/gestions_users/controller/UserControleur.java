package com.example.schoolgestapp.gestions_users.controller;

import com.example.schoolgestapp.entity.User;
import com.example.schoolgestapp.entity.Teacher;
import com.example.schoolgestapp.entity.enums.Role;
import com.example.schoolgestapp.gestions_users.dto.BatchStatusUpdateRequest;
import com.example.schoolgestapp.gestions_users.dto.TeacherDTO;
import com.example.schoolgestapp.gestions_users.dto.UserResponseDTO;
import com.example.schoolgestapp.gestions_users.services.UserService;
import com.example.schoolgestapp.repository.ITeacher;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@Tag(name = "03. Utilisateurs & Administration", description = "Gestion globale des comptes, des rôles et des statuts (Admin uniquement)")
public class UserControleur {

    private final UserService userService;
    private final ITeacher teacherRepository;

    public UserControleur(UserService userService, ITeacher teacherRepository) {
        this.userService = userService;
        this.teacherRepository = teacherRepository;
    }

    // --- Recherche & Filtrage ---
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> searchUsers(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(userService.searchUsers(query, role, active));
    }

    @GetMapping("/teachers")
    public ResponseEntity<List<TeacherDTO>> getTeachers() {
        List<TeacherDTO> teachers = teacherRepository.findAll().stream()
                .map(this::toTeacherDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(u -> ResponseEntity.ok(userService.convertToDTO(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.save(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        return ResponseEntity.ok(userService.save(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Actions administratives ---
    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponseDTO> updateStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        return ResponseEntity.ok(userService.updateStatus(id, body.get("active")));
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponseDTO> updateRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Role role = Role.valueOf(body.get("role").toUpperCase());
        return ResponseEntity.ok(userService.updateRole(id, role));
    }

    // --- Batch Operations ---
    @PostMapping("/batch/status")
    public ResponseEntity<Void> toggleStatusBatch(@RequestBody BatchStatusUpdateRequest request) {
        userService.toggleStatusInBatch(request.getIds(), request.isActive());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/batch/delete")
    public ResponseEntity<Void> deleteBatch(@RequestBody Map<String, List<Long>> body) {
        userService.deleteInBatch(body.get("ids"));
        return ResponseEntity.noContent().build();
    }

    private TeacherDTO toTeacherDTO(Teacher t) {
        if (t == null) return null;
        return TeacherDTO.builder()
                .id(t.getId())
                .userId(t.getUser() != null ? t.getUser().getId() : null)
                .name(t.getUser() != null ? t.getUser().getFirstName() + " " + t.getUser().getLastName() : null)
                .email(t.getUser() != null ? t.getUser().getEmail() : null)
                .phone(t.getUser() != null ? t.getUser().getPhone() : null)
                .specialties(t.getSpecialties())
                .office(t.getOffice())
                .departmentName(t.getDepartment() != null ? t.getDepartment().getName() : null)
                .cvUrl(t.getCvUrl())
                .hireDate(t.getHireDate())
                .build();
    }
}
