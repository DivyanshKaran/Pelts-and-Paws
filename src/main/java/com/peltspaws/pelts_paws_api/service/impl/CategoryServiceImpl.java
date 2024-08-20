package com.peltspaws.pelts_paws_api.service.impl;

import com.peltspaws.pelts_paws_api.dto.category.CategoryRequest;
import com.peltspaws.pelts_paws_api.dto.category.CategoryResponse;
import com.peltspaws.pelts_paws_api.entity.Category;
import com.peltspaws.pelts_paws_api.exception.BadRequestException;
import com.peltspaws.pelts_paws_api.repository.CategoryRepository;
import com.peltspaws.pelts_paws_api.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new com.peltspaws.pelts_paws_api.exception.ResourceNotFoundException(
                        "Category not found with id: " + id));
        return toDto(category);
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new BadRequestException("Category '" + request.getName() + "' already exists");
        }
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        return toDto(categoryRepository.save(category));
    }

    private CategoryResponse toDto(Category c) {
        return CategoryResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .build();
    }
}
