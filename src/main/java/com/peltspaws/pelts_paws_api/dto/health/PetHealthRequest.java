package com.peltspaws.pelts_paws_api.dto.health;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PetHealthRequest {
    private Double weight;
    private Double height;
    private LocalDate lastCheckup;
    private List<String> vaccinations;
    private String notes;
}
