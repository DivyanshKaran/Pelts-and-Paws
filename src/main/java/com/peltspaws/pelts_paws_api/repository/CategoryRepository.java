package com.peltspaws.pelts_paws_api.repository;

import com.peltspaws.pelts_paws_api.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
}
