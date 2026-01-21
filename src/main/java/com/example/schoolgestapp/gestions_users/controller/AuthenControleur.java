package com.example.schoolgestapp.gestions_users.controller;

import com.example.schoolgestapp.entity.User;
import com.example.schoolgestapp.gestions_ressources.services.FileUploadService;
import com.example.schoolgestapp.gestions_users.dto.*;
import com.example.schoolgestapp.gestions_users.services.Authservice;
import com.example.schoolgestapp.gestions_users.services.PasswordResetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * CONTROLEUR : Authentification et Profil Utilisateur.
 * Intègre désormais l'upload d'avatar (photo de profil) via Cloudinary.
 */


@RestController
@RequestMapping("/api/auth")
@Tag(name = "01. Authentification", description = "Endpoints sécurisés pour l'inscription, la connexion (JWT) et la gestion de compte")
public class AuthenControleur {

    private final Authservice authservice;
    private final PasswordResetService passwordResetService;
    private final FileUploadService fileUploadService;
    private final ObjectMapper objectMapper;

    public AuthenControleur(Authservice authservice, 
                            PasswordResetService passwordResetService, 
                            FileUploadService fileUploadService, 
                            ObjectMapper objectMapper) {
        this.authservice = authservice;
        this.passwordResetService = passwordResetService;
        this.fileUploadService = fileUploadService;
        this.objectMapper = objectMapper;
    }

    /**
     * Inscription avec support de photo de profil.
     */
    @Operation(summary = "Inscription d'un nouvel utilisateur", description = "Données JSON dans 'data' et fichier image dans 'photo'")
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AuthResponseDTO> register(
            @RequestPart("data") String dataJson,
            @RequestPart(value = "photo", required = false) MultipartFile photo) throws Exception {
        
        RegisterRequest request = objectMapper.readValue(dataJson, RegisterRequest.class);
        
        // Si une photo est fournie, on l'upload sur Cloudinary
        if (photo != null && !photo.isEmpty()) {
            String avatarUrl = fileUploadService.uploadFile(photo, "avatars");
            request.setAvatarUrl(avatarUrl);
        }
        
        return ResponseEntity.ok(authservice.register(request));
    }

    @Operation(summary = "Connexion utilisateur", description = "Retourne un token JWT et les infos de base")
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authservice.login(loginRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(authservice.refreshToken(body.get("refreshToken")));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> body) {
        String token = passwordResetService.initiatePasswordReset(body.get("email"));
        return ResponseEntity.ok(Map.of("message", "Email de récupération envoyé", "token", token));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> body) {
        passwordResetService.resetPasswordWithToken(body.get("token"), body.get("newPassword"));
        return ResponseEntity.ok(Map.of("message", "Mot de passe réinitialisé avec succès"));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMe() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(authservice.getFullProfile(username));
    }

    /**
     * Mise à jour du profil avec possibilité de changer de photo.
     */
    @PutMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponseDTO> updateProfile(
            @RequestPart("data") String dataJson,
            @RequestPart(value = "photo", required = false) MultipartFile photo) throws Exception {
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        @SuppressWarnings("unchecked")
        Map<String, String> body = objectMapper.readValue(dataJson, Map.class);
        
        if (photo != null && !photo.isEmpty()) {
            String avatarUrl = fileUploadService.uploadFile(photo, "avatars");
            body.put("avatarUrl", avatarUrl);
        }
        
        User updatedUser = authservice.updateProfile(username, body);
        return ResponseEntity.ok(authservice.toUserResponseDTO(updatedUser));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody Map<String, String> body) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        authservice.changePassword(username, body.get("oldPassword"), body.get("newPassword"));
        return ResponseEntity.ok(Map.of("message", "Mot de passe modifié avec succès"));
    }
}
