package com.peltspaws.pelts_paws_api.service;

import com.peltspaws.pelts_paws_api.dto.pet.PetRequest;
import com.peltspaws.pelts_paws_api.dto.pet.PetResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PetService {
    Page<PetResponse> getAllPets(String species, String breed, Pageable pageable);
    PetResponse getPetById(Long id);
    PetResponse createPet(PetRequest request, String ownerEmail);
    PetResponse updatePet(Long id, PetRequest request, String ownerEmail);
    void deletePet(Long id, String ownerEmail);
    List<PetResponse> getMyPets(String ownerEmail);
}
