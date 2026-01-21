package com.example.schoolgestapp.gestions_ressources.services;

import com.example.schoolgestapp.entity.*;
import com.example.schoolgestapp.gestions_ressources.dto.ResourceDTO;
import com.example.schoolgestapp.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des ressources et documents.
 */
@Service
@Transactional
public class ResourceService {

    private final IResource resourceRepository;
    private final ISubject subjectRepository;
    private final IClasse classeRepository;

    public ResourceService(IResource resourceRepository, 
                           ISubject subjectRepository, 
                           IClasse classeRepository) {
        this.resourceRepository = resourceRepository;
        this.subjectRepository = subjectRepository;
        this.classeRepository = classeRepository;
    }

    // --- Enseignant : Partage de ressources ---
    public ResourceDTO addResource(Resource resource) {
        return toDTO(resourceRepository.save(resource));
    }

    public void deleteResource(Long id) {
        resourceRepository.deleteById(id);
    }

    // --- Consultation ---
    public List<ResourceDTO> getAllResources() {
        return resourceRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ResourceDTO> getResourcesBySubject(Long subjectId) {
        Subject subject = subjectRepository.getReferenceById(subjectId);
        return resourceRepository.findBySubject(subject).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ResourceDTO> getResourcesForStudent(Long studentId, Long classeId) {
        Classe classe = classeRepository.getReferenceById(classeId);
        return resourceRepository.findByClasse(classe).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ResourceDTO updateResource(Long id, Resource updatedResource) {
        Resource existing = resourceRepository.findById(id).orElseThrow();
        existing.setTitle(updatedResource.getTitle());
        existing.setDescription(updatedResource.getDescription());
        existing.setFileUrl(updatedResource.getFileUrl());
        existing.setType(updatedResource.getType());
        existing.setPublished(updatedResource.isPublished());
        return toDTO(resourceRepository.save(existing));
    }

    /* ===================== mapping methode ================= */

    private ResourceDTO toDTO(Resource r) {
        if (r == null) return null;
        return ResourceDTO.builder()
                .id(r.getId())
                .title(r.getTitle())
                .description(r.getDescription())
                .fileUrl(r.getFileUrl())
                .type(r.getType() != null ? r.getType().name() : null)
                .subjectId(r.getSubject() != null ? r.getSubject().getId() : null)
                .subjectName(r.getSubject() != null ? r.getSubject().getName() : null)
                .teacherId(r.getTeacher() != null ? r.getTeacher().getId() : null)
                .teacherName(r.getTeacher() != null ? r.getTeacher().getUser().getFirstName() + " " + r.getTeacher().getUser().getLastName() : null)
                .classeId(r.getClasse() != null ? r.getClasse().getId() : null)
                .classeName(r.getClasse() != null ? r.getClasse().getName() : null)
                .published(r.isPublished())
                .createdAt(r.getUploadDate())
                .build();
    }
}
