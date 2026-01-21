package com.example.schoolgestapp.gestions_communications.services;

import com.example.schoolgestapp.entity.*;
import com.example.schoolgestapp.exception.BusinessException;
import com.example.schoolgestapp.gestions_communications.dto.MessageDTO;
import com.example.schoolgestapp.gestions_communications.dto.NotificationDTO;
import com.example.schoolgestapp.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la messagerie et les notifications - SÉCURISÉ.
 */
@Service
@Transactional
public class CommunicationService {

    private final IMessage messageRepository;
    private final INotification notificationRepository;
    private final IUser userRepository;
    private final EmailService emailService;

    public CommunicationService(IMessage messageRepository, 
                                INotification notificationRepository, 
                                IUser userRepository,
                                EmailService emailService) {
        this.messageRepository = messageRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public MessageDTO sendMessage(Message message) {
        return toMessageDTO(messageRepository.save(message));
    }

    public List<MessageDTO> getInbox(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable"));
        return messageRepository.findByReceiverOrderBySentAtDesc(user).stream()
                .map(this::toMessageDTO)
                .collect(Collectors.toList());
    }

    public List<MessageDTO> getSentMessages(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable"));
        return messageRepository.findBySenderOrderBySentAtDesc(user).stream()
                .map(this::toMessageDTO)
                .collect(Collectors.toList());
    }

    public void markMessageAsRead(Long messageId) {
        Message msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new BusinessException("Message introuvable"));
        msg.setRead(true);
        msg.setReadAt(LocalDateTime.now());
        messageRepository.save(msg);
    }

    public NotificationDTO sendNotification(Long userId, String title, String message, String type, String actionUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable"));
        Notification notif = new Notification();
        notif.setUser(user);
        notif.setTitle(title);
        notif.setMessage(message);
        notif.setType(type);
        notif.setActionUrl(actionUrl);
        
        // Envoi d'une alerte email en parallèle
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            String htmlContent = "<h3>" + title + "</h3>" +
                    "<p>" + message + "</p>" +
                    "<a href=\"" + actionUrl + "\">Voir les détails</a>";
            emailService.sendEmail(user.getEmail(), "Notification SchoolGest : " + title, htmlContent);
        }
        
        return toNotificationDTO(notificationRepository.save(notif));
    }

    public List<NotificationDTO> getUserNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable"));
        return notificationRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::toNotificationDTO)
                .collect(Collectors.toList());
    }

    public void markNotificationAsRead(Long notifId) {
        Notification notif = notificationRepository.findById(notifId)
                .orElseThrow(() -> new BusinessException("Notification introuvable"));
        notif.setRead(true);
        notificationRepository.save(notif);
    }

    public long getUnreadCounts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable"));
        return messageRepository.countByReceiverAndIsReadFalse(user) + 
               notificationRepository.countByUserAndIsReadFalse(user);
    }

    /* ===================== mapping methode ================= */

    private MessageDTO toMessageDTO(Message m) {
        if (m == null) return null;
        return MessageDTO.builder()
                .id(m.getId())
                .senderId(m.getSender() != null ? m.getSender().getId() : null)
                .senderName(m.getSender() != null ? m.getSender().getFirstName() + " " + m.getSender().getLastName() : null)
                .receiverId(m.getReceiver() != null ? m.getReceiver().getId() : null)
                .receiverName(m.getReceiver() != null ? m.getReceiver().getFirstName() + " " + m.getReceiver().getLastName() : null)
                .subject(m.getSubject())
                .content(m.getContent())
                .sentAt(m.getSentAt())
                .read(m.isRead())
                .readAt(m.getReadAt())
                .build();
    }

    private NotificationDTO toNotificationDTO(Notification n) {
        if (n == null) return null;
        return NotificationDTO.builder()
                .id(n.getId())
                .userId(n.getUser() != null ? n.getUser().getId() : null)
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType())
                .actionUrl(n.getActionUrl())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
