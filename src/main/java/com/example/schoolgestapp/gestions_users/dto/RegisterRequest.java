package com.example.schoolgestapp.gestions_users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Requête d'inscription utilisateur.
 */
@Data
public class RegisterRequest {

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit faire au moins 6 caractères")
    private String password;

    private String role; // Optionnel, par défaut ETUDIANT

    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String avatarUrl;

    // Champs spécifiques selon le profil
    private String birthDate;   // Pour Etudiant
    private String gender;      // Pour Etudiant
    private String hireDate;    // Pour Enseignant
    private String studentCode; // Pour Etudiant (généré si fourni)
    private String teacherCode; // Pour Enseignant (généré si fourni)
}
