package com.openclassrooms.etudiant.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudentResponseDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}