package com.example.Ecommerce.repository;

import com.example.Ecommerce.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepo extends JpaRepository<Category,Integer> {
    Category findByCategoryName(String categoryName);
}
