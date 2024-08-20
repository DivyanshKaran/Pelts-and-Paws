package com.peltspaws.pelts_paws_api.controller;

import com.peltspaws.pelts_paws_api.dto.health.PetHealthRequest;
import com.peltspaws.pelts_paws_api.dto.health.PetHealthResponse;
import com.peltspaws.pelts_paws_api.service.PetHealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pets/{petId}/health")
@RequiredArgsConstructor
public class PetHealthController {

    private final PetHealthService petHealthService;

    /** GET /api/pets/{petId}/health — public (guarded by SecurityConfig GET permit) */
    @GetMapping
    public ResponseEntity<PetHealthResponse> getHealth(@PathVariable Long petId) {
        return ResponseEntity.ok(petHealthService.getHealthByPetId(petId));
    }

    /** PUT /api/pets/{petId}/health — upsert (owner or public) */
    @PutMapping
    public ResponseEntity<PetHealthResponse> upsertHealth(@PathVariable Long petId,
                                                           @RequestBody PetHealthRequest request,
                                                           @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(petHealthService.upsertHealth(petId, request, email));
    }
}
