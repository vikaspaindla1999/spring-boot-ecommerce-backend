package com.example.Ecommerce.controller;

import com.example.Ecommerce.config.AppConstants;
import com.example.Ecommerce.model.Category;
import com.example.Ecommerce.payload.CategoryDTO;
import com.example.Ecommerce.payload.CategoryResponse;
import com.example.Ecommerce.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
   @Tag(name = "Category APIs",description = "API for managing Categories")
   @Operation(summary = "Get all Categories",description = "API to get all categories")
    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getCategories(@RequestParam(name = "pageNumber",defaultValue = AppConstants.PAGE_NUMBER, required = false)Integer pageNumber,
                                                          @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE, required = false)Integer pageSize,
                                                          @RequestParam(name = "sortBy",defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy,
                                                          @RequestParam(name = "sortOrder",defaultValue = AppConstants.SORT_DIRECTION, required = false)String sortOrder){
        CategoryResponse response =categoryService.getCategories(pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
    @Tag(name = "Category APIs",description = "API for managing Categories")
    @Operation(summary = "Create Category",description = "API to create a category")
    @ApiResponses({@ApiResponse(responseCode = "201",description = "Category created successfully"),
                    @ApiResponse(responseCode = "400",description = "Invalid Input")})
    @PostMapping("/admin/category")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO){
        CategoryDTO savedCategory=categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(savedCategory,HttpStatus.CREATED);
    }
    @Tag(name = "Category APIs",description = "API for managing Categories")
    @Operation(summary = "Update Category",description = "API to update a category by Id")
    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO,@PathVariable Integer categoryId){
       CategoryDTO updatedCategory= categoryService.updateCategory(categoryDTO,categoryId);
        return new ResponseEntity<>(updatedCategory,HttpStatus.OK);
    }
    @Tag(name = "Category APIs",description = "API for managing Categories")
    @Operation(summary = "Delete Category",description = "API to delete a category by Id")
    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Integer categoryId){
        CategoryDTO deletedCategory=categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(deletedCategory,HttpStatus.OK);
    }

}
