package com.example.schoolgestapp.gestions_academique.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstablishmentDTO {
    private Long id;
    private String name;
    private String code;
    private String address;
    private String phone;
    private String email;
    private String website;
    private String logoUrl;
    private String academicYears;
    private String academicParameters;
}
