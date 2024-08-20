package com.peltspaws.pelts_paws_api.dto.health;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class PetHealthResponse {
    private Long id;
    private Long petId;
    private String petName;
    private Double weight;
    private Double height;
    private LocalDate lastCheckup;
    private List<String> vaccinations;
    private String notes;
    private LocalDateTime createdAt;
}
