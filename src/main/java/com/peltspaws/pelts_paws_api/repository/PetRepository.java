package com.peltspaws.pelts_paws_api.repository;

import com.peltspaws.pelts_paws_api.entity.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByOwnerId(Long ownerId);

    // Used by service to compose filters without fragile JPQL null-binding
    Page<Pet> findBySpeciesIgnoreCase(String species, Pageable pageable);

    Page<Pet> findByBreedContainingIgnoreCase(String breed, Pageable pageable);

    Page<Pet> findBySpeciesIgnoreCaseAndBreedContainingIgnoreCase(
            String species, String breed, Pageable pageable);
}
