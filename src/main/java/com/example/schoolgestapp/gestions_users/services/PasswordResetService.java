package com.example.schoolgestapp.gestions_users.services;

import com.example.schoolgestapp.entity.User;
import com.example.schoolgestapp.gestions_communications.services.EmailService;
import com.example.schoolgestapp.repository.IUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service de gestion de la réinitialisation des mots de passe.
 * Ce service implémente une logique réaliste avec génération de tokens,
 * expiration et validation sécurisée.
 */
@Service
public class PasswordResetService {

    private final IUser userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @org.springframework.beans.factory.annotation.Value("${application.frontend.url}")
    private String frontendUrl;

    // Durée de validité du token (ex: 1 heure)
    private static final int TOKEN_EXPIRATION_HOURS = 1;

    public PasswordResetService(IUser userRepository, 
                                PasswordEncoder passwordEncoder,
                                EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    /**
     * Initie la procédure de réinitialisation.
     * Génère un token unique, définit son expiration et l'enregistre en base.
     * 
     * @param email L'adresse email de l'utilisateur.
     * @return Le token généré (pourrait être envoyé par email dans un système réel).
     */
    @Transactional
    public String initiatePasswordReset(String email) {
        // 1. Rechercher l'utilisateur par son email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Aucun compte n'est associé à cette adresse email."));

        // 2. Générer un token unique de type UUID
        String token = UUID.randomUUID().toString();

        // 3. Définir le token et sa date d'expiration (ex: maintenant + 1h)
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(TOKEN_EXPIRATION_HOURS));

        // 4. Sauvegarder les modifications
        // 4. Sauvegarder les modifications
        userRepository.save(user);

        // Envoi de l'email réel
        String link = frontendUrl + "/reset-password?token=" + token;
        String htmlContent = "<h3>Réinitialisation du mot de passe</h3>" +
                "<p>Bonjour,</p>" +
                "<p>Vous avez demandé la réinitialisation de votre mot de passe.</p>" +
                "<p>Veuillez cliquer sur le lien ci-dessous pour changer votre mot de passe :</p>" +
                "<a href=\"" + link + "\">Réinitialiser mon mot de passe</a>" +
                "<p>Ce lien est valide pour 1 heure.</p>";

        emailService.sendEmail(email, "Réinitialisation de mot de passe - SchoolGest", htmlContent);

        return token;
    }

    /**
     * Finalise la réinitialisation après vérification de la validité du token.
     * 
     * @param token Le token reçu par l'utilisateur.
     * @param newPassword Le nouveau mot de passe choisi.
     */
    @Transactional
    public void resetPasswordWithToken(String token, String newPassword) {
        // 1. Rechercher l'utilisateur par son token de réinitialisation
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Le lien de réinitialisation est invalide."));

        // 2. Vérifier si le token n'a pas expiré
        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Le lien de réinitialisation a expiré.");
        }

        // 3. Hasher et mettre à jour le nouveau mot de passe
        user.setPassword(passwordEncoder.encode(newPassword));

        // 4. Invalider le token après utilisation (sécurité)
        user.setResetToken(null);
        user.setResetTokenExpiry(null);

        // 5. Sauvegarder l'utilisateur
        userRepository.save(user);
    }
}
