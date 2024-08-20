package com.peltspaws.pelts_paws_api.service;

import com.peltspaws.pelts_paws_api.dto.health.PetHealthRequest;
import com.peltspaws.pelts_paws_api.dto.health.PetHealthResponse;

public interface PetHealthService {
    PetHealthResponse getHealthByPetId(Long petId);
    PetHealthResponse upsertHealth(Long petId, PetHealthRequest request, String ownerEmail);
}
