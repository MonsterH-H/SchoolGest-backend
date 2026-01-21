package com.example.schoolgestapp.gestions_communications.controller;

import com.example.schoolgestapp.entity.Message;
import com.example.schoolgestapp.gestions_communications.dto.MessageDTO;
import com.example.schoolgestapp.gestions_communications.dto.NotificationDTO;
import com.example.schoolgestapp.gestions_communications.services.CommunicationService;
import com.example.schoolgestapp.gestions_ressources.services.FileUploadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * CONTROLEUR : Communications avec support d'envoi de fichiers.
 */
@RestController
@RequestMapping("/api/communications")
@Tag(name = "11. Communications & Notifications", description = "Messagerie interne, envoi de pièces jointes et gestion des notifications temps-réel")
public class CommunicationController {

    private final CommunicationService communicationService;
    private final FileUploadService fileUploadService;
    private final ObjectMapper objectMapper;

    public CommunicationController(CommunicationService communicationService, 
                                   FileUploadService fileUploadService, 
                                   ObjectMapper objectMapper) {
        this.communicationService = communicationService;
        this.fileUploadService = fileUploadService;
        this.objectMapper = objectMapper;
    }

    // --- Envoyer un message avec une pièce jointe ---
    @PostMapping(value = "/messages", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageDTO> sendWithFile(
            @RequestPart("message") String messageJson,
            @RequestPart(value = "file", required = false) MultipartFile file) throws Exception {
        
        Message message = objectMapper.readValue(messageJson, Message.class);
        
        if (file != null && !file.isEmpty()) {
            String url = fileUploadService.uploadFile(file, "messages");
            message.setAttachmentUrl(url);
        }
        
        return ResponseEntity.ok(communicationService.sendMessage(message));
    }

    @GetMapping("/boite-reception/{userId}")
    public ResponseEntity<List<MessageDTO>> getInbox(@PathVariable Long userId) {
        return ResponseEntity.ok(communicationService.getInbox(userId));
    }

    @PatchMapping("/messages/{id}/lire")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        communicationService.markMessageAsRead(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/notifications/{userId}")
    public ResponseEntity<List<NotificationDTO>> getNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(communicationService.getUserNotifications(userId));
    }

    @GetMapping("/non-lus/{userId}")
    public ResponseEntity<Long> getUnreadCount(@PathVariable Long userId) {
        return ResponseEntity.ok(communicationService.getUnreadCounts(userId));
    }
}
