package com.peltspaws.pelts_paws_api.service;

import com.peltspaws.pelts_paws_api.dto.gem.AwardGemRequest;
import com.peltspaws.pelts_paws_api.dto.gem.GemResponse;

import java.util.List;

public interface GemService {
    List<GemResponse> getMyGems(String userEmail);
    GemResponse awardGems(AwardGemRequest request, String adminEmail);
    Integer getGemTotalForPet(Long petId);
}
