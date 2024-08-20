package com.peltspaws.pelts_paws_api.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
}
