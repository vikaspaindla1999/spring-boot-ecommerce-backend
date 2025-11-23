package com.example.Ecommerce.service;

import com.example.Ecommerce.exceptions.APIException;
import com.example.Ecommerce.exceptions.ResourceNotFoundException;
import com.example.Ecommerce.model.Category;
import com.example.Ecommerce.payload.CategoryDTO;
import com.example.Ecommerce.payload.CategoryResponse;
import com.example.Ecommerce.repository.CategoryRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{
    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public CategoryResponse getCategories(Integer pageNumber,Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        Pageable pageable= PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Category> categoryPage=categoryRepo.findAll(pageable);
        List<Category> categories=categoryPage.getContent();

        if(categories.isEmpty())
            throw new APIException("No Categories Found");
        List<CategoryDTO> categoryDTOS=categories.stream().map(category -> modelMapper.map(category, CategoryDTO.class)).toList();

        CategoryResponse categoryResponse=new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());

        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category= modelMapper.map(categoryDTO,Category.class);
        Category existingCategory=categoryRepo.findByCategoryName(categoryDTO.getCategoryName());
        if(existingCategory!=null)
            throw new APIException("Category with the name: "+categoryDTO.getCategoryName()+" already exists");

        Category savedCategory=categoryRepo.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Integer categoryId) {

        Category existingCategory=categoryRepo.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("Category","CategoryId",categoryId));
        existingCategory.setCategoryName(categoryDTO.getCategoryName());
       Category savedCategory= categoryRepo.save(existingCategory);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Integer categoryId) {
        Category existingCategory=categoryRepo.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("Category","CategoryId",categoryId));;
        categoryRepo.delete(existingCategory);
        return modelMapper.map(existingCategory,CategoryDTO.class);

    }

}
