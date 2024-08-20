package com.peltspaws.pelts_paws_api.service.impl;

import com.peltspaws.pelts_paws_api.dto.health.PetHealthRequest;
import com.peltspaws.pelts_paws_api.dto.health.PetHealthResponse;
import com.peltspaws.pelts_paws_api.entity.Pet;
import com.peltspaws.pelts_paws_api.entity.PetHealth;
import com.peltspaws.pelts_paws_api.exception.ResourceNotFoundException;
import com.peltspaws.pelts_paws_api.exception.UnauthorizedException;
import com.peltspaws.pelts_paws_api.repository.PetHealthRepository;
import com.peltspaws.pelts_paws_api.repository.PetRepository;
import com.peltspaws.pelts_paws_api.service.PetHealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@Transactional
@RequiredArgsConstructor
public class PetHealthServiceImpl implements PetHealthService {

    private final PetHealthRepository petHealthRepository;
    private final PetRepository petRepository;

    @Override
    public PetHealthResponse getHealthByPetId(Long petId) {
        PetHealth health = petHealthRepository.findByPetId(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Health record not found for pet id: " + petId));
        return toDto(health);
    }

    @Override
    public PetHealthResponse upsertHealth(Long petId, PetHealthRequest request, String ownerEmail) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + petId));

        if (!pet.getOwner().getEmail().equals(ownerEmail)) {
            throw new UnauthorizedException("You are not the owner of this pet");
        }

        PetHealth health = petHealthRepository.findByPetId(petId)
                .orElse(PetHealth.builder().pet(pet).vaccinations(new ArrayList<>()).build());

        if (request.getWeight() != null)       health.setWeight(request.getWeight());
        if (request.getHeight() != null)       health.setHeight(request.getHeight());
        if (request.getLastCheckup() != null)  health.setLastCheckup(request.getLastCheckup());
        if (request.getNotes() != null)        health.setNotes(request.getNotes());
        if (request.getVaccinations() != null) health.setVaccinations(request.getVaccinations());

        return toDto(petHealthRepository.save(health));
    }

    private PetHealthResponse toDto(PetHealth h) {
        return PetHealthResponse.builder()
                .id(h.getId())
                .petId(h.getPet().getId())
                .petName(h.getPet().getName())
                .weight(h.getWeight())
                .height(h.getHeight())
                .lastCheckup(h.getLastCheckup())
                .vaccinations(h.getVaccinations())
                .notes(h.getNotes())
                .createdAt(h.getCreatedAt())
                .build();
    }
}
