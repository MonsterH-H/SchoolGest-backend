package com.example.schoolgestapp.gestions_communications.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Long id;
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String receiverName;
    private String subject;
    private String content;
    private LocalDateTime sentAt;
    private boolean read;
    private LocalDateTime readAt;
}
