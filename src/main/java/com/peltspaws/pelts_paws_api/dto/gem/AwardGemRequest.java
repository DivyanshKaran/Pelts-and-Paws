package com.peltspaws.pelts_paws_api.dto.gem;

import com.peltspaws.pelts_paws_api.entity.Gem;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AwardGemRequest {

    @NotNull(message = "petId is required")
    private Long petId;

    @NotNull(message = "type is required")
    private Gem.GemType type;

    @NotNull(message = "amount is required")
    @Min(value = 1, message = "Amount must be at least 1")
    private Integer amount;
}
