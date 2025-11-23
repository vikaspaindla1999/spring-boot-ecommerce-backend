package com.example.Ecommerce.controller;

import com.example.Ecommerce.config.AppConstants;
import com.example.Ecommerce.model.Product;
import com.example.Ecommerce.payload.ProductDTO;
import com.example.Ecommerce.payload.ProductResponse;
import com.example.Ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController{
    @Autowired
    private ProductService productService;
    @Tag(name = "Product APIs",description = "API for managing Products")
    @Operation(summary = "Add Product",description = "API to add a new product under a specific category")
    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO, @PathVariable Integer categoryId){
        ProductDTO savedProduct=productService.addProduct(categoryId,productDTO);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }
    @Tag(name = "Product APIs",description = "API for managing Products")
    @Operation(summary = "Get all Products",description = "API to get all products")
    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(@RequestParam(defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
                                                          @RequestParam(defaultValue = AppConstants.SORT_DIRECTION, required = false) String sortOrder,
                                                          @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                          @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize){

        ProductResponse productResponse=productService.getAllProducts(sortBy,sortOrder,pageNumber,pageSize);
        return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }
    @Tag(name = "Product APIs",description = "API for managing Products")
    @Operation(summary = "Search product by Category",description = "API to search a product by category")
    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> searchByCategory(@PathVariable Integer categoryId,
                                                            @RequestParam(defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
                                                            @RequestParam(defaultValue = AppConstants.SORT_DIRECTION, required = false) String sortOrder,
                                                            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize){
        ProductResponse productResponse=productService.searchByCategory(categoryId,sortBy,sortOrder,pageNumber,pageSize);
        return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }
    @Tag(name = "Product APIs",description = "API for managing Products")
    @Operation(summary = "Search Product by keyword",description = "API to search a product by keyword")
    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> searchByKeyword(@PathVariable String keyword,
                                                           @RequestParam(defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
                                                           @RequestParam(defaultValue = AppConstants.SORT_DIRECTION, required = false) String sortOrder,
                                                           @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                           @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize){
        ProductResponse productResponse=productService.searchByKeyword(keyword,sortBy,sortOrder,pageNumber,pageSize);
        return new ResponseEntity<>(productResponse,HttpStatus.FOUND);
    }
    @Tag(name = "Product APIs",description = "API for managing Products")
    @Operation(summary = "Update Product",description = "API to update Product")
    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@Valid @RequestBody ProductDTO productDTO,@PathVariable Integer productId){
        ProductDTO savedProduct=productService.updateProduct(productDTO,productId);
        return new ResponseEntity<>(savedProduct,HttpStatus.CREATED);
    }
    @Tag(name = "Product APIs",description = "API for managing Products")
    @Operation(summary = "Delete Product",description = "API to delete product")
    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Integer productId){
        productService.deleteProduct(productId);
        return new ResponseEntity<>("Deleted sucessfully",HttpStatus.OK);
    }
    @Tag(name = "Product APIs",description = "API for managing Products")
    @Operation(summary = "update product image",description = "API to update product image")
    @PutMapping("/admin/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Integer productId, @RequestParam MultipartFile image) throws IOException {
        ProductDTO updatedProduct=productService.updateProductImage(productId,image);
        return new ResponseEntity<>(updatedProduct,HttpStatus.CREATED);
    }

}
