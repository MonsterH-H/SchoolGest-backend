package com.example.schoolgestapp.gestions_ressources.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.schoolgestapp.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * SERVICE : Stockage en ligne (Cloudinary)
 * Permet d'envoyer des fichiers physiques vers le cloud et de récupérer une URL sécurisée.
 */
@Service
public class FileUploadService {

    private final Cloudinary cloudinary;

    public FileUploadService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Upload un fichier sur Cloudinary et retourne l'URL publique.
     * 
     * @param file Fichier physique (MultipartFile) récupéré du contrôleur.
     * @param folder Nom du dossier dans le cloud (ex: "justificatifs", "cours").
     * @return URL sécurisée du fichier dans le cloud.
     */
    public String uploadFile(MultipartFile file, String folder) {
        try {
            if (file == null || file.isEmpty()) {
                throw new BusinessException("Le fichier est vide ou inexistant");
            }

            // Options d'upload : dossier et type automatique (pdf, img, etc)
            Map<?, ?> options = ObjectUtils.asMap(
                "folder", "schoolgest/" + folder,
                "resource_type", "auto"
            );

            // Upload effectif vers Cloudinary
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
            
            // Retourne l'URL HTTPS générée par Cloudinary
            return (String) uploadResult.get("secure_url");

        } catch (IOException e) {
            throw new BusinessException("Erreur technique lors de l'upload cloud : " + e.getMessage());
        }
    }
}
