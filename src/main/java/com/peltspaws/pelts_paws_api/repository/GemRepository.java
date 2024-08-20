package com.peltspaws.pelts_paws_api.repository;

import com.peltspaws.pelts_paws_api.entity.Gem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GemRepository extends JpaRepository<Gem, Long> {

    List<Gem> findByUserIdOrderByEarnedAtDesc(Long userId);

    List<Gem> findByPetId(Long petId);

    @Query("SELECT COALESCE(SUM(g.amount), 0) FROM Gem g WHERE g.pet.id = :petId")
    Integer sumAmountByPetId(@Param("petId") Long petId);
}
