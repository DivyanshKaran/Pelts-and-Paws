package com.peltspaws.pelts_paws_api.service;

import com.peltspaws.pelts_paws_api.dto.category.CategoryRequest;
import com.peltspaws.pelts_paws_api.dto.category.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();
    CategoryResponse getCategoryById(Long id);
    CategoryResponse createCategory(CategoryRequest request);
}
