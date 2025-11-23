package com.example.Ecommerce.controller;

import com.example.Ecommerce.model.Cart;
import com.example.Ecommerce.payload.CartDTO;
import com.example.Ecommerce.repository.CartRepository;
import com.example.Ecommerce.service.CartService;
import com.example.Ecommerce.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AuthUtil authUtil;
    @Tag(name = "Cart APIs",description = "API for managing carts")
    @Operation(summary = "Add Products to Cart",description = "API to add products to cart")
    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Integer productId,@PathVariable Integer quantity){
        CartDTO cart=cartService.addProductToCart(productId,quantity);
        return new ResponseEntity<CartDTO>(cart, HttpStatus.CREATED);
    }
    @Tag(name = "Cart APIs",description = "API for managing carts")
    @Operation(summary = "Get Carts",description = "API to get Carts")
    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getCarts(){
        List<CartDTO> cartDTOS=cartService.getCarts();
        return new ResponseEntity<List<CartDTO>>(cartDTOS,HttpStatus.OK);
    }
    @Tag(name = "Cart APIs",description = "API for managing carts")
    @Operation(summary = "Get Cart by logged in user",description = "API to get cart by logged in user")
    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getCartById(){
        String emailId=authUtil.loggedInEmail();
        Cart cart=cartRepository.findCartByEmail(emailId);
        Integer cartId=cart.getCartId();
        CartDTO cartDTO=cartService.getCartById(emailId,cartId);
        return new ResponseEntity<CartDTO>(cartDTO,HttpStatus.OK);
    }

    @Tag(name = "Cart APIs",description = "API for managing carts")
    @Operation(summary = "Update Product quantity in Cart",description = "API to Update Product quantity in Cart")
    @PutMapping("/carts/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateCartProductQuantityInCart(@PathVariable Integer productId,@PathVariable String operation){
        CartDTO cartDTO=cartService.updateCartProductQuantityInCart(productId,operation.equalsIgnoreCase("delete")?-1:1);
        return new ResponseEntity<CartDTO>(cartDTO,HttpStatus.OK);
    }
    @Tag(name = "Cart APIs",description = "API for managing carts")
    @Operation(summary = "Delete products from cart",description = "API to delete products from cart")
    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Integer cartId,@PathVariable Integer productId){
        String status=cartService.deleteProductFromCart(cartId,productId);
        return new ResponseEntity<>(status,HttpStatus.OK);
    }
}
