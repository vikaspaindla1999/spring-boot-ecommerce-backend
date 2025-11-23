package com.example.Ecommerce.service;

import com.example.Ecommerce.model.Product;
import com.example.Ecommerce.payload.ProductDTO;
import com.example.Ecommerce.payload.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductDTO addProduct(Integer id, ProductDTO productDTO);

    ProductResponse getAllProducts(String sortBy, String sortOrder,Integer pageNumber, Integer pageSize);

    ProductResponse searchByCategory(Integer categoryId,String sortBy, String sortOrder,Integer pageNumber, Integer pageSize);

    ProductResponse searchByKeyword(String keyword,String sortBy, String sortOrder,Integer pageNumber, Integer pageSize);

    ProductDTO updateProduct(ProductDTO productDTO, Integer id);

    void deleteProduct(Integer productId);

    ProductDTO updateProductImage(Integer productId, MultipartFile image) throws IOException;
}
