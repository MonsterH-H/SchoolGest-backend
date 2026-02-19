package com.example.schoolgestapp.gestions_users.controller;

import com.example.schoolgestapp.common.dto.PagedResponse;
import com.example.schoolgestapp.entity.User;
import com.example.schoolgestapp.entity.enums.Role;
import com.example.schoolgestapp.gestions_users.dto.BatchStatusUpdateRequest;
import com.example.schoolgestapp.gestions_users.dto.TeacherDTO;
import com.example.schoolgestapp.gestions_users.dto.UserResponseDTO;
import com.example.schoolgestapp.gestions_users.mapper.TeacherMapper;
import com.example.schoolgestapp.gestions_users.services.UserService;
import com.example.schoolgestapp.repository.ITeacher;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "03. Utilisateurs & Administration", description = "Gestion globale des comptes, des roles et des statuts (Admin uniquement)")
public class UserControleur {

    private final UserService userService;
    private final ITeacher teacherRepository;
    private final TeacherMapper teacherMapper;

    public UserControleur(UserService userService, ITeacher teacherRepository, TeacherMapper teacherMapper) {
        this.userService = userService;
        this.teacherRepository = teacherRepository;
        this.teacherMapper = teacherMapper;
    }

    // --- Recherche & Filtrage ---
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> searchUsers(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(userService.searchUsers(query, role, active));
    }

    @GetMapping("/paged")
    public ResponseEntity<PagedResponse<UserResponseDTO>> searchUsersPaged(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Pageable pageable = buildPageable(page, size, sortBy, direction);
        Page<UserResponseDTO> result = userService.searchUsersPaged(query, role, active, pageable);
        return ResponseEntity.ok(PagedResponse.from(result));
    }

    @GetMapping("/teachers")
    public ResponseEntity<List<TeacherDTO>> getTeachers() {
        List<TeacherDTO> teachers = teacherRepository.findAll().stream()
                .map(teacherMapper::toDto)
                .toList();
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/teachers/paged")
    public ResponseEntity<PagedResponse<TeacherDTO>> getTeachersPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Pageable pageable = buildPageable(page, size, sortBy, direction);
        Page<TeacherDTO> result = teacherRepository.findAll(pageable).map(teacherMapper::toDto);
        return ResponseEntity.ok(PagedResponse.from(result));
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

    private Pageable buildPageable(int page, int size, String sortBy, String direction) {
        int safeSize = Math.min(Math.max(size, 1), 100);
        Sort sort = "asc".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(Math.max(page, 0), safeSize, sort);
    }
}
