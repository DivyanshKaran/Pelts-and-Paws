package com.peltspaws.pelts_paws_api.dto.pet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class PetResponse {
    private Long id;
    private String name;
    private String species;
    private String breed;
    private Integer age;
    private String gender;
    private String description;
    private String imageUrl;
    private Long ownerId;
    private String ownerUsername;
    private Long categoryId;
    private String categoryName;
    private Integer gems;
    private LocalDateTime createdAt;
}
