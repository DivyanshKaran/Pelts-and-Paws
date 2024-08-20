package com.peltspaws.pelts_paws_api.controller;

import com.peltspaws.pelts_paws_api.dto.gem.AwardGemRequest;
import com.peltspaws.pelts_paws_api.dto.gem.GemResponse;
import com.peltspaws.pelts_paws_api.service.GemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class GemController {

    private final GemService gemService;

    /** GET /api/gems/my — current user's gem history */
    @GetMapping("/api/gems/my")
    public ResponseEntity<List<GemResponse>> getMyGems(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(gemService.getMyGems(userDetails.getUsername()));
    }

    /** POST /api/gems/award — ADMIN only */
    @PostMapping("/api/gems/award")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GemResponse> awardGems(@Valid @RequestBody AwardGemRequest request,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(gemService.awardGems(request, userDetails.getUsername()));
    }

    /** GET /api/pets/{petId}/gems — total gems for a pet */
    @GetMapping("/api/pets/{petId}/gems")
    public ResponseEntity<Map<String, Integer>> getGemTotal(@PathVariable Long petId) {
        return ResponseEntity.ok(Map.of("total", gemService.getGemTotalForPet(petId)));
    }
}
