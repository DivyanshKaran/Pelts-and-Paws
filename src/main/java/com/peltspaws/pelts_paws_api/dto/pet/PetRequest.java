package com.peltspaws.pelts_paws_api.dto.pet;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PetRequest {

    @NotBlank(message = "Pet name is required")
    private String name;

    @NotBlank(message = "Species is required")
    private String species;

    private String breed;

    @Min(value = 0, message = "Age must be non-negative")
    private Integer age;

    private String gender;

    private String description;

    private String imageUrl;

    private Long categoryId;
}
