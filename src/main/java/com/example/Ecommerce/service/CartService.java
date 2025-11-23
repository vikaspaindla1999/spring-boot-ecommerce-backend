package com.example.Ecommerce.service;

import com.example.Ecommerce.payload.CartDTO;
import jakarta.transaction.Transactional;

import java.util.List;

public interface CartService {
    CartDTO addProductToCart(Integer productId, Integer quantity);

    List<CartDTO> getCarts();

    CartDTO getCartById(String emailId, Integer cartId);
    @Transactional
    CartDTO updateCartProductQuantityInCart(Integer productId, int quantity);

    String deleteProductFromCart(Integer cartId, Integer productId);

    void updateProductInCarts(Integer cartId, Integer productId);
}
