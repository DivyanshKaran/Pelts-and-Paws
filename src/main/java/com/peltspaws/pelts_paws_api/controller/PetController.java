package com.peltspaws.pelts_paws_api.controller;

import com.peltspaws.pelts_paws_api.dto.pet.PetRequest;
import com.peltspaws.pelts_paws_api.dto.pet.PetResponse;
import com.peltspaws.pelts_paws_api.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    /** GET /api/pets?species=dog&breed=lab&page=0&size=10 */
    @GetMapping
    public ResponseEntity<Page<PetResponse>> getAllPets(
            @RequestParam(required = false) String species,
            @RequestParam(required = false) String breed,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(petService.getAllPets(species, breed, pageable));
    }

    /** GET /api/pets/my — secured */
    @GetMapping("/my")
    public ResponseEntity<List<PetResponse>> getMyPets(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(petService.getMyPets(userDetails.getUsername()));
    }

    /** GET /api/pets/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<PetResponse> getPetById(@PathVariable Long id) {
        return ResponseEntity.ok(petService.getPetById(id));
    }

    /** POST /api/pets — secured */
    @PostMapping
    public ResponseEntity<PetResponse> createPet(@Valid @RequestBody PetRequest request,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(petService.createPet(request, userDetails.getUsername()));
    }

    /** PUT /api/pets/{id} — owner only */
    @PutMapping("/{id}")
    public ResponseEntity<PetResponse> updatePet(@PathVariable Long id,
                                                   @Valid @RequestBody PetRequest request,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(petService.updatePet(id, request, userDetails.getUsername()));
    }

    /** DELETE /api/pets/{id} — owner or ADMIN */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable Long id,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        petService.deletePet(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
