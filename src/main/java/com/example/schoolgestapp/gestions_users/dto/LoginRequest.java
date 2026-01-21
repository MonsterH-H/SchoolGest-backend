package com.example.schoolgestapp.gestions_users.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Requête de connexion.
 */
@Data
public class LoginRequest {
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    private String username;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;
}
