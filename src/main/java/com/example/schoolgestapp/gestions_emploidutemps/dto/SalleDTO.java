package com.example.schoolgestapp.gestions_emploidutemps.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalleDTO {
    private Long id;
    private String nom;
    private String batiment;
    private Integer capacite;
    private String type;
}
