package com.example.schoolgestapp.gestions_users.dto;

import lombok.Data;
import java.util.List;

/**
 * DTO pour les opérations de mise à jour de statut en masse
 */
@Data
public class BatchStatusUpdateRequest {
    private List<Long> ids;
    private boolean active;
}
