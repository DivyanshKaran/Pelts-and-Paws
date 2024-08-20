package com.peltspaws.pelts_paws_api.service.impl;

import com.peltspaws.pelts_paws_api.dto.pet.PetRequest;
import com.peltspaws.pelts_paws_api.dto.pet.PetResponse;
import com.peltspaws.pelts_paws_api.entity.Category;
import com.peltspaws.pelts_paws_api.entity.Pet;
import com.peltspaws.pelts_paws_api.entity.User;
import com.peltspaws.pelts_paws_api.exception.ResourceNotFoundException;
import com.peltspaws.pelts_paws_api.exception.UnauthorizedException;
import com.peltspaws.pelts_paws_api.repository.CategoryRepository;
import com.peltspaws.pelts_paws_api.repository.PetRepository;
import com.peltspaws.pelts_paws_api.repository.UserRepository;
import com.peltspaws.pelts_paws_api.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Page<PetResponse> getAllPets(String species, String breed, Pageable pageable) {
        boolean hasSpecies = species != null && !species.isBlank();
        boolean hasBreed   = breed   != null && !breed.isBlank();

        Page<Pet> page;
        if (hasSpecies && hasBreed) {
            page = petRepository.findBySpeciesIgnoreCaseAndBreedContainingIgnoreCase(species, breed, pageable);
        } else if (hasSpecies) {
            page = petRepository.findBySpeciesIgnoreCase(species, pageable);
        } else if (hasBreed) {
            page = petRepository.findByBreedContainingIgnoreCase(breed, pageable);
        } else {
            page = petRepository.findAll(pageable);
        }
        return page.map(this::toDto);
    }

    @Override
    public PetResponse getPetById(Long id) {
        return toDto(findPetById(id));
    }

    @Override
    public PetResponse createPet(PetRequest request, String ownerEmail) {
        User owner = findUserByEmail(ownerEmail);
        Category category = resolveCategory(request.getCategoryId());

        Pet pet = Pet.builder()
                .name(request.getName())
                .species(request.getSpecies())
                .breed(request.getBreed())
                .age(request.getAge())
                .gender(request.getGender())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .owner(owner)
                .category(category)
                .gems(0)
                .build();

        return toDto(petRepository.save(pet));
    }

    @Override
    public PetResponse updatePet(Long id, PetRequest request, String ownerEmail) {
        Pet pet = findPetById(id);
        assertOwner(pet, ownerEmail);

        pet.setName(request.getName());
        pet.setSpecies(request.getSpecies());
        pet.setBreed(request.getBreed());
        pet.setAge(request.getAge());
        pet.setGender(request.getGender());
        pet.setDescription(request.getDescription());
        pet.setImageUrl(request.getImageUrl());
        pet.setCategory(resolveCategory(request.getCategoryId()));

        return toDto(petRepository.save(pet));
    }

    @Override
    public void deletePet(Long id, String callerEmail) {
        Pet pet = findPetById(id);
        User caller = findUserByEmail(callerEmail);

        boolean isOwner = pet.getOwner().getId().equals(caller.getId());
        boolean isAdmin = caller.getRole() == User.Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new UnauthorizedException("You are not allowed to delete this pet");
        }

        petRepository.delete(pet);
    }

    @Override
    public List<PetResponse> getMyPets(String ownerEmail) {
        User owner = findUserByEmail(ownerEmail);
        return petRepository.findByOwnerId(owner.getId()).stream().map(this::toDto).toList();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Pet findPetById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private Category resolveCategory(Long categoryId) {
        if (categoryId == null) return null;
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
    }

    private void assertOwner(Pet pet, String email) {
        if (!pet.getOwner().getEmail().equals(email)) {
            throw new UnauthorizedException("You are not the owner of this pet");
        }
    }

    private PetResponse toDto(Pet pet) {
        return PetResponse.builder()
                .id(pet.getId())
                .name(pet.getName())
                .species(pet.getSpecies())
                .breed(pet.getBreed())
                .age(pet.getAge())
                .gender(pet.getGender())
                .description(pet.getDescription())
                .imageUrl(pet.getImageUrl())
                .ownerId(pet.getOwner().getId())
                .ownerUsername(pet.getOwner().getUsername())
                .categoryId(pet.getCategory() != null ? pet.getCategory().getId() : null)
                .categoryName(pet.getCategory() != null ? pet.getCategory().getName() : null)
                .gems(pet.getGems())
                .createdAt(pet.getCreatedAt())
                .build();
    }
}
