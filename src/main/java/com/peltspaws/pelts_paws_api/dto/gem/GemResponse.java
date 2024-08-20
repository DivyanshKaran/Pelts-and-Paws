package com.peltspaws.pelts_paws_api.dto.gem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class GemResponse {
    private Long id;
    private Long petId;
    private String petName;
    private Long userId;
    private String type;
    private Integer amount;
    private LocalDateTime earnedAt;
}
