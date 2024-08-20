package com.peltspaws.pelts_paws_api.repository;

import com.peltspaws.pelts_paws_api.entity.PetHealth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PetHealthRepository extends JpaRepository<PetHealth, Long> {
    Optional<PetHealth> findByPetId(Long petId);
}
