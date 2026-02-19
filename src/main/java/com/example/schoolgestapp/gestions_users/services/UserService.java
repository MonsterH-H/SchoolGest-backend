package com.example.schoolgestapp.gestions_users.services;

import com.example.schoolgestapp.entity.User;
import com.example.schoolgestapp.entity.enums.Role;
import com.example.schoolgestapp.gestions_users.dto.UserResponseDTO;
import com.example.schoolgestapp.gestions_users.mapper.UserMapper;
import com.example.schoolgestapp.repository.IUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * Service de gestion administrative des utilisateurs.
 * Permet la recherche avancee, le filtrage et les operations en masse.
 */
@Service
public class UserService {

    private final IUser userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(IUser userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public List<UserResponseDTO> findAll() {
        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    // --- Recherche & Filtrage ---
    public List<UserResponseDTO> searchUsers(String query, String roleStr, Boolean active) {
        return searchUsersPaged(query, roleStr, active, Pageable.unpaged()).getContent();
    }

    public Page<UserResponseDTO> searchUsersPaged(String query, String roleStr, Boolean active, Pageable pageable) {
        Specification<User> spec = buildSpecification(query, roleStr, active);
        return userRepository.findAll(spec, pageable).map(userMapper::toDto);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public UserResponseDTO save(User user) {
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    // --- Operations individuelles ---
    @Transactional
    public UserResponseDTO updateStatus(Long id, boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouve"));
        user.setActive(active);
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public UserResponseDTO updateRole(Long id, Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouve"));
        user.setRole(role);
        return userMapper.toDto(userRepository.save(user));
    }

    // --- Operations en Masse (Batch) ---
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
        return userMapper.toDto(user);
    }

    private Specification<User> buildSpecification(String query, String roleStr, Boolean active) {
        return Specification.where(searchQuerySpec(query))
                .and(roleSpec(roleStr))
                .and(activeSpec(active));
    }

    private Specification<User> searchQuerySpec(String query) {
        if (!StringUtils.hasText(query)) {
            return null;
        }
        String likeValue = "%" + query.toLowerCase() + "%";
        return (root, cq, cb) -> cb.or(
                cb.like(cb.lower(root.get("username")), likeValue),
                cb.like(cb.lower(root.get("email")), likeValue),
                cb.like(cb.lower(root.get("firstName")), likeValue),
                cb.like(cb.lower(root.get("lastName")), likeValue)
        );
    }

    private Specification<User> roleSpec(String roleStr) {
        if (!StringUtils.hasText(roleStr)) {
            return null;
        }
        Role role = Role.valueOf(roleStr.toUpperCase());
        return (root, cq, cb) -> cb.equal(root.get("role"), role);
    }

    private Specification<User> activeSpec(Boolean active) {
        if (active == null) {
            return null;
        }
        return (root, cq, cb) -> cb.equal(root.get("active"), active);
    }
}
