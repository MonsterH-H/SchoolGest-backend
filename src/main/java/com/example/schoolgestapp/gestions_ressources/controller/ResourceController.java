package com.example.schoolgestapp.gestions_ressources.controller;

import com.example.schoolgestapp.entity.Resource;
import com.example.schoolgestapp.gestions_ressources.dto.ResourceDTO;
import com.example.schoolgestapp.gestions_ressources.services.FileUploadService;
import com.example.schoolgestapp.gestions_ressources.services.ResourceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * CONTROLEUR : Gestion des Ressources (Cours) avec upload Cloud.
 */
@RestController
@RequestMapping("/api/ressources")
@Tag(name = "08. Ressources Pédagogiques", description = "Mise en ligne et consultation des supports de cours (PDF, Vidéos, documents)")
public class ResourceController {

    private final ResourceService resourceService;
    private final FileUploadService fileUploadService;
    private final ObjectMapper objectMapper;

    public ResourceController(ResourceService resourceService, FileUploadService fileUploadService, ObjectMapper objectMapper) {
        this.resourceService = resourceService;
        this.fileUploadService = fileUploadService;
        this.objectMapper = objectMapper;
    }

    // --- Créer une ressource (Cours PDF/Vidéo) avec upload ---
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResourceDTO> createWithFile(
            @RequestPart("resource") String resourceJson,
            @RequestPart(value = "file", required = false) MultipartFile file) throws Exception {
        
        Resource resource = objectMapper.readValue(resourceJson, Resource.class);
        
        if (file != null && !file.isEmpty()) {
            String url = fileUploadService.uploadFile(file, "cours");
            resource.setFileUrl(url);
        }
        
        return ResponseEntity.ok(resourceService.addResource(resource));
    }

    @GetMapping
    public ResponseEntity<List<ResourceDTO>> getAll() {
        return ResponseEntity.ok(resourceService.getAllResources());
    }

    @GetMapping("/matiere/{subjectId}")
    public ResponseEntity<List<ResourceDTO>> getBySubject(@PathVariable Long subjectId) {
        return ResponseEntity.ok(resourceService.getResourcesBySubject(subjectId));
    }

    @GetMapping("/classe/{classeId}")
    public ResponseEntity<List<ResourceDTO>> getByClasse(@PathVariable Long classeId, @RequestParam Long studentId) {
        return ResponseEntity.ok(resourceService.getResourcesForStudent(studentId, classeId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.ok().build();
    }
}
