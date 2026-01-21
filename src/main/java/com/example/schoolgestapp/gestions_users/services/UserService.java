package com.example.schoolgestapp.gestions_users.services;

import com.example.schoolgestapp.entity.User;
import com.example.schoolgestapp.entity.enums.Role;
import com.example.schoolgestapp.gestions_users.dto.UserResponseDTO;
import com.example.schoolgestapp.repository.IUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service de gestion administrative des utilisateurs.
 * Permet la recherche avancée, le filtrage et les opérations en masse.
 */
@Service
public class UserService {

    private final IUser userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(IUser userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponseDTO> findAll() {
        return userRepository.findAll().stream().map(this::toUserResponseDTO).collect(Collectors.toList());
    }

    // --- Recherche & Filtrage ---
    public List<UserResponseDTO> searchUsers(String query, String roleStr, Boolean active) {
        return userRepository.findAll().stream()
                .filter(u -> query == null || u.getUsername().contains(query) || u.getEmail().contains(query))
                .filter(u -> roleStr == null || u.getRole().name().equalsIgnoreCase(roleStr))
                .filter(u -> active == null || u.isActive() == active)
                .map(this::toUserResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public UserResponseDTO save(User user) {
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return toUserResponseDTO(userRepository.save(user));
    }

    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    // --- Opérations individuelles ---
    @Transactional
    public UserResponseDTO updateStatus(Long id, boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        user.setActive(active);
        return toUserResponseDTO(userRepository.save(user));
    }

    @Transactional
    public UserResponseDTO updateRole(Long id, Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        user.setRole(role);
        return toUserResponseDTO(userRepository.save(user));
    }

    // --- Opérations en Masse (Batch) ---
    @Transactional
    public void toggleStatusInBatch(List<Long> ids, boolean active) {
        List<User> users = userRepository.findAllById(ids);
        users.forEach(u -> u.setActive(active));
        userRepository.saveAll(users);
    }

    @Transactional
    public void deleteInBatch(List<Long> ids) {
        userRepository.deleteAllById(ids);
    }

    /**
     * Simulation d'import en masse (CSV/Excel).
     */
    @Transactional
    public void importUsers(List<User> data) {
        data.forEach(this::save);
    }

    public UserResponseDTO convertToDTO(User user) {
        return toUserResponseDTO(user);
    }

    private UserResponseDTO toUserResponseDTO(User user) {
        if (user == null) return null;
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .active(user.isActive())
                .phone(user.getPhone())
                .address(user.getAddress())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .build();
    }
}
