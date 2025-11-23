package com.example.Ecommerce.service;

import com.example.Ecommerce.model.Category;
import com.example.Ecommerce.payload.CategoryDTO;
import com.example.Ecommerce.payload.CategoryResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CategoryService {
    CategoryResponse getCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO updateCategory(CategoryDTO categoryDTO, Integer categoryId);

    CategoryDTO deleteCategory(Integer categoryId);

}
