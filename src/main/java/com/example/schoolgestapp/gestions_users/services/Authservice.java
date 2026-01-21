package com.example.schoolgestapp.gestions_users.services;

import com.example.schoolgestapp.entity.*;
import com.example.schoolgestapp.entity.enums.Role;
import com.example.schoolgestapp.gestions_users.dto.*;
import com.example.schoolgestapp.repository.*;
import com.example.schoolgestapp.security.JwtService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service d'authentification avancé.
 * Gère l'inscription, la connexion sécurisée, le verrouillage de compte et l'audit.
 */
@Service
public class Authservice {

    private final IUser userRepository;
    private final ITeacher teacherRepository;
    private final IStudent studentRepository;
    private final IAdmin adminRepository;
    private final ISystemLog logRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_TIME_MINUTES = 15;

    public Authservice(
            IUser userRepository, ITeacher teacherRepository, IStudent studentRepository,
            IAdmin adminRepository, ISystemLog logRepository, PasswordEncoder passwordEncoder,
            JwtService jwtService, UserDetailsService userDetailsService
    ) {
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.adminRepository = adminRepository;
        this.logRepository = logRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Inscription multi-profils.
     * Crée un utilisateur et son profil associé (etudiant, Enseignant ou Admin).
     */
    @Transactional
    public AuthResponseDTO register(RegisterRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        String email = request.getEmail();
        String roleStr = request.getRole();

        if (userRepository.existsByUsername(username) || userRepository.existsByEmail(email)) {
            throw new RuntimeException("Nom d'utilisateur ou email déjà utilisé");
        }

        Role role = Role.ETUDIANT;
        try {
            if (roleStr != null) role = Role.valueOf(roleStr.toUpperCase());
        } catch (Exception ignored) {}

        // 1. Création de l'utilisateur
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setActive(false); // Par défaut, inactif (en attente de validation admin)
        user.setEmailVerified(false);
        user.setVerificationToken(UUID.randomUUID().toString());
        
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        User savedUser = userRepository.save(user);

        // 2. Création du profil spécifique
        createLinkedProfile(savedUser, role, request);

        // 3. Log d'audit
        createAuditLog(savedUser, "REGISTER", "Inscription réussie (en attente de validation)");

        return AuthResponseDTO.builder()
                .accessToken(null) // Pas de login automatique
                .refreshToken(null)
                .user(toUserResponseDTO(savedUser))
                .build();
    }

    /**
     * Connexion sécurisée avec gestion du verrouillage.
     */
    @Transactional
    public AuthResponseDTO login(LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Vérification du verrouillage
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Compte verrouillé. Réessayez dans " + LOCK_TIME_MINUTES + " minutes.");
        }

        // Vérification du mot de passe
        if (!passwordEncoder.matches(password, user.getPassword())) {
            handleFailedLogin(user);
            throw new RuntimeException("Mot de passe incorrect (" + (MAX_FAILED_ATTEMPTS - user.getFailedLoginAttempts()) + " essais restants)");
        }

        // Réussite : Reset des tentatives
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Audit
        createAuditLog(user, "LOGIN", "Connexion réussie");

        // Génération des tokens
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Map<String, Object> claims = Map.of("role", user.getRole().name());
        String accessToken = jwtService.generateToken(userDetails, claims);
        
        // Refresh Token
        String refreshToken = UUID.randomUUID().toString();
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(7));
        userRepository.save(user);

        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(toUserResponseDTO(user))
                .build();
    }

    /**
     * Rafraîchir l'access token via le refresh token.
     */
    @Transactional
    public AuthResponseDTO refreshToken(String refreshToken) {
        // Optimisation : Recherche directe par token en base de données
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh Token invalide"));

        if (user.getRefreshTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh Token expiré. Veuillez vous reconnecter.");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        Map<String, Object> claims = Map.of("role", user.getRole().name());
        String newAccessToken = jwtService.generateToken(userDetails, claims);

        return AuthResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .user(toUserResponseDTO(user))
                .build();
    }

    private void handleFailedLogin(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);
        if (attempts >= MAX_FAILED_ATTEMPTS) {
            user.setLockedUntil(LocalDateTime.now().plusMinutes(LOCK_TIME_MINUTES));
            createAuditLog(user, "ACCOUNT_LOCKED", "Compte verrouillé après 5 échecs");
        }
        userRepository.save(user);
        createAuditLog(user, "LOGIN_FAILED", "Tentative échouée (" + attempts + ")");
    }

    /**
     * Crée le profil spécifique lié à l'utilisateur selon son rôle.
     * Tente de récupérer des informations complémentaires si présentes dans le body.
     */
    private void createLinkedProfile(User user, Role role, RegisterRequest request) {
        switch (role) {
            case ETUDIANT -> {
                Student s = new Student();
                s.setUser(user);
                s.setStudentCode(request.getStudentCode() != null ? request.getStudentCode() : "STU-" + UUID.randomUUID().toString().substring(0, 8));
                
                if (request.getBirthDate() != null) {
                    try { s.setBirthDate(java.time.LocalDate.parse(request.getBirthDate())); } catch (Exception ignored) {}
                }
                if (request.getGender() != null) {
                    try { s.setGender(com.example.schoolgestapp.entity.enums.Sexe.valueOf(request.getGender().toUpperCase())); } catch (Exception ignored) {}
                }
                
                studentRepository.save(s);
            }
            case ENSEIGNANT -> {
                Teacher t = new Teacher();
                t.setUser(user);
                t.setTeacherCode(request.getTeacherCode() != null ? request.getTeacherCode() : "TCH-" + UUID.randomUUID().toString().substring(0, 8));
                
                if (request.getHireDate() != null) {
                    try { t.setHireDate(java.time.LocalDate.parse(request.getHireDate())); } catch (Exception ignored) {}
                }
                
                teacherRepository.save(t);
            }
            case ADMIN -> {
                Admin a = new Admin();
                a.setUser(user);
                a.setName(user.getFirstName() != null ? user.getFirstName() + " " + user.getLastName() : user.getUsername());
                adminRepository.save(a);
            }
        }
    }

    private void createAuditLog(User user, String action, String desc) {
        SystemLog log = new SystemLog();
        log.setUser(user);
        log.setAction(action);
        log.setDescription(desc);
        logRepository.save(log);
    }

    public User updateProfile(String username, Map<String, String> body) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (body.containsKey("firstName")) user.setFirstName(body.get("firstName"));
        if (body.containsKey("lastName")) user.setLastName(body.get("lastName"));
        if (body.containsKey("phone")) user.setPhone(body.get("phone"));
        if (body.containsKey("address")) user.setAddress(body.get("address"));
        if (body.containsKey("avatarUrl")) user.setAvatarUrl(body.get("avatarUrl"));

        // Mise à jour du profil étudiant si applicable
        if (user.getRole() == Role.ETUDIANT) {
            studentRepository.findByUser(user).ifPresent(student -> {
                if (body.containsKey("nationality")) {
                    student.setNationality(body.get("nationality"));
                }
                studentRepository.save(student);
            });
        }

        return userRepository.save(user);
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Ancien mot de passe incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        createAuditLog(user, "CHANGE_PASSWORD", "Mot de passe modifié avec succès");
    }

    /**
     * Récupère le profil complet (User + Détails spécifiques Student/Teacher/Admin)
     */
    public Map<String, Object> getFullProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Map<String, Object> response = new HashMap<>();
        response.put("profile", toUserResponseDTO(user));
        
        switch (user.getRole()) {
            case ETUDIANT -> {
                Student s = studentRepository.findByUser(user).orElse(null);
                response.put("academicDetails", toStudentDTO(s));
            }
            case ENSEIGNANT -> {
                Teacher t = teacherRepository.findByUser(user).orElse(null);
                response.put("academicDetails", toTeacherDTO(t));
            }
            case ADMIN -> {
                Admin a = adminRepository.findByUser(user).orElse(null);
                response.put("academicDetails", toAdminDTO(a));
            }
        }

        return response;
    }

    private AdminDTO toAdminDTO(Admin a) {
        if (a == null) return null;
        return AdminDTO.builder()
                .id(a.getId())
                .userId(a.getUser().getId())
                .name(a.getName())
                .email(a.getUser().getEmail())
                .phone(a.getPhone())
                .address(a.getAddress())
                .admin_poste(a.getAdmin_poste())
                .build();
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

    public UserResponseDTO toUserResponseDTO(User user) {
        UserResponseDTO dto = UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .build();

        // Ajout des détails académiques selon le rôle
        if (user.getRole() != null) {
            switch (user.getRole()) {
                case ENSEIGNANT -> teacherRepository.findByUser(user)
                        .ifPresent(t -> dto.setAcademicDetails(toTeacherDTO(t)));
                case ETUDIANT -> studentRepository.findByUser(user)
                        .ifPresent(s -> dto.setAcademicDetails(toStudentDTO(s)));
                case ADMIN -> adminRepository.findByUser(user)
                        .ifPresent(a -> dto.setAcademicDetails(toAdminDTO(a)));
            }
        }

        return dto;
    }
}
