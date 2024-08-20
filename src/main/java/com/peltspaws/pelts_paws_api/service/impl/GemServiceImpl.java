package com.peltspaws.pelts_paws_api.service.impl;

import com.peltspaws.pelts_paws_api.dto.gem.AwardGemRequest;
import com.peltspaws.pelts_paws_api.dto.gem.GemResponse;
import com.peltspaws.pelts_paws_api.entity.Gem;
import com.peltspaws.pelts_paws_api.entity.Pet;
import com.peltspaws.pelts_paws_api.entity.User;
import com.peltspaws.pelts_paws_api.exception.ResourceNotFoundException;
import com.peltspaws.pelts_paws_api.repository.GemRepository;
import com.peltspaws.pelts_paws_api.repository.PetRepository;
import com.peltspaws.pelts_paws_api.repository.UserRepository;
import com.peltspaws.pelts_paws_api.service.GemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class GemServiceImpl implements GemService {

    private final GemRepository gemRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;

    @Override
    public List<GemResponse> getMyGems(String userEmail) {
        User user = findUserByEmail(userEmail);
        return gemRepository.findByUserIdOrderByEarnedAtDesc(user.getId())
                .stream().map(this::toDto).toList();
    }

    @Override
    @Transactional
    public GemResponse awardGems(AwardGemRequest request, String adminEmail) {
        User admin = findUserByEmail(adminEmail);
        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + request.getPetId()));

        Gem gem = Gem.builder()
                .pet(pet)
                .user(admin)
                .type(request.getType())
                .amount(request.getAmount())
                .build();

        gemRepository.save(gem);

        // Also increment the denormalized gems counter on Pet
        pet.setGems(pet.getGems() + request.getAmount());
        petRepository.save(pet);

        return toDto(gem);
    }

    @Override
    public Integer getGemTotalForPet(Long petId) {
        if (!petRepository.existsById(petId)) {
            throw new ResourceNotFoundException("Pet not found with id: " + petId);
        }
        return gemRepository.sumAmountByPetId(petId);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private GemResponse toDto(Gem gem) {
        return GemResponse.builder()
                .id(gem.getId())
                .petId(gem.getPet().getId())
                .petName(gem.getPet().getName())
                .userId(gem.getUser().getId())
                .type(gem.getType().name())
                .amount(gem.getAmount())
                .earnedAt(gem.getEarnedAt())
                .build();
    }
}
