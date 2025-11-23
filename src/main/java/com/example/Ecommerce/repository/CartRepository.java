package com.example.Ecommerce.repository;

import com.example.Ecommerce.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart,Integer> {

    @Query("SELECT c FROM Cart c WHERE c.user.email=?1")
    Cart findCartByEmail(String email);
    @Query("SELECT c FROM Cart c WHERE c.user.email=?1 AND c.id=?2")
    Cart findCartByEmailAndCardId(String emailId, Integer cartId);
    @Query("SELECT c FROM Cart c JOIN FETCH c.cartItems ci JOIN FETCH ci.product p WHERE p.id=?1")
    List<Cart> findCartsByProductId(Integer productId);
}
