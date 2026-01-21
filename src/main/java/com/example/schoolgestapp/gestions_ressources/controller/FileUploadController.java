package com.example.schoolgestapp.gestions_ressources.controller;

import com.example.schoolgestapp.gestions_ressources.services.FileUploadService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * CONTROLEUR : Upload de fichiers en ligne
 */
@RestController
@RequestMapping("/api/stockage")
@Tag(name = "13. Stockage & Fichiers", description = "Service Cloud de gestion des fichiers (Images, PDF, Documents) via Cloudinary")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    /**
     * Endpoint générique pour uploader un fichier.
     * @param file Le fichier physique envoyé via FormData.
     * @param dossier Le nom du dossier cible (ex: "avatars", "devoirs", "justificatifs").
     * @return L'URL du fichier uploadé.
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file, 
                                                     @RequestParam(value = "dossier", defaultValue = "general") String dossier) {
        String url = fileUploadService.uploadFile(file, dossier);
        
        Map<String, String> response = new HashMap<>();
        response.put("url", url);
        response.put("status", "SUCCESS");
        
        return ResponseEntity.ok(response);
    }
}
