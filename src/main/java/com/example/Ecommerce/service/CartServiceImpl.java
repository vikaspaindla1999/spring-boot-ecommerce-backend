package com.example.Ecommerce.service;

import com.example.Ecommerce.exceptions.APIException;
import com.example.Ecommerce.exceptions.ResourceNotFoundException;
import com.example.Ecommerce.model.Cart;
import com.example.Ecommerce.model.CartItem;
import com.example.Ecommerce.model.Product;
import com.example.Ecommerce.payload.CartDTO;
import com.example.Ecommerce.payload.ProductDTO;
import com.example.Ecommerce.repository.CartItemRepository;
import com.example.Ecommerce.repository.CartRepository;
import com.example.Ecommerce.repository.ProductRepo;
import com.example.Ecommerce.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@Service
public class CartServiceImpl implements CartService{

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthUtil authUtil;
    @Override
    public CartDTO addProductToCart(Integer productId, Integer quantity) {

        Cart cart=createCart();
        Product product=productRepo.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product","Product Id",productId));

        CartItem cartItem=cartItemRepository.findCartItemByCartIdAndProductId(cart.getCartId(),productId);

        if(cartItem!=null){
            throw new APIException("Product "+product.getProductName()+" is already exists in the cart");
        }
        if(product.getQuantity()==0){
            throw new APIException(product.getProductName()+" currently not available");
        }
        if(product.getQuantity()<quantity){
            throw new APIException("Please make an order of the "+product.getProductName()+" less than or equal to product quantity "+product.getQuantity());
        }

        CartItem newCartItem=new CartItem();
        newCartItem.setCart(cart);
        newCartItem.setProduct(product);
        newCartItem.setQuantity(quantity);
        newCartItem.setProductPrice(product.getSpecialPrice());
        newCartItem.setDiscount(product.getDiscount());

        cartItemRepository.save(newCartItem);
        cart.setTotalPrice(cart.getTotalPrice()+(product.getSpecialPrice()*quantity));
        cartRepository.save(cart);

        CartDTO cartDTO=modelMapper.map(cart,CartDTO.class);
        List<CartItem> cartItemList=cart.getCartItems();


        Stream<ProductDTO> productStream=cartItemList.stream().map(item->{
            ProductDTO map=modelMapper.map(item.getProduct(),ProductDTO.class);
            map.setQuantity(quantity);
            return map;
        });

        cartDTO.setProducts(productStream.toList());

        return cartDTO;
    }

    @Override
    public List<CartDTO> getCarts() {
        List<Cart> carts=cartRepository.findAll();

        if (carts.isEmpty()){
            throw  new APIException("No Cart Found");
        }
        List<CartDTO> cartDTOS=carts.stream().map(item->{
            CartDTO cartDTO=modelMapper.map(item,CartDTO.class);
            List<ProductDTO> productDTOS=item.getCartItems().stream().map(p->{
                ProductDTO productDTO=modelMapper.map(p.getProduct(),ProductDTO.class);
                productDTO.setQuantity(p.getQuantity());
                return productDTO;
            }).collect(Collectors.toList());

            cartDTO.setProducts(productDTOS);

            return cartDTO;
        }).toList();
        return cartDTOS;
    }

    @Override
    public CartDTO getCartById(String emailId, Integer cartId) {
        Cart cart=cartRepository.findCartByEmailAndCardId(emailId,cartId);
        CartDTO cartDTO=modelMapper.map(cart,CartDTO.class);
        cart.getCartItems().forEach(item->item.getProduct().setQuantity(item.getQuantity()));
        List<ProductDTO> productDTOS=cart.getCartItems().stream().map(item-> modelMapper.map(item.getProduct(),ProductDTO.class)).toList();
        cartDTO.setProducts(productDTOS);
        return cartDTO;
    }

    @Override
    @Transactional
    public CartDTO updateCartProductQuantityInCart(Integer productId, int quantity) {
        String userMail= authUtil.loggedInEmail();
        Cart userCart=cartRepository.findCartByEmail(userMail);
        Integer cartId=userCart.getCartId();

        Cart cart=cartRepository.findById(cartId).orElseThrow(()->new ResourceNotFoundException("Cart","CartId",cartId));
        Product product=productRepo.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product","Product Id",productId));
        if(product.getQuantity()==0){
            throw new APIException(product.getProductName()+" currently not available");
        }
        if(product.getQuantity()<quantity){
            throw new APIException("Please make an order of the "+product.getProductName()+" less than or equal to product quantity "+product.getQuantity());
        }

        CartItem cartItem=cartItemRepository.findCartItemByCartIdAndProductId(cartId,productId);

        if(cartItem==null){
            throw new APIException("Product "+product.getProductName()+" is not available in the cart");
        }

        int newQuantity=cartItem.getQuantity()+quantity;
        if(newQuantity<0){
            throw new APIException("Product quantity cannot be negative");
        }
        if(newQuantity==0){
            deleteProductFromCart(cartId,productId);
        }
        else {

            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));
            cartRepository.save(cart);
            cartItemRepository.save(cartItem);
        }

        CartDTO cartDTO=modelMapper.map(cart,CartDTO.class);
        List<CartItem> cartItems=cart.getCartItems();
        Stream<ProductDTO> productDTOStream=cartItems.stream().map(item->{
            ProductDTO prod=modelMapper.map(item.getProduct(),ProductDTO.class);
            prod.setQuantity(item.getQuantity());
            return prod;
        });

        cartDTO.setProducts(productDTOStream.toList());

        return cartDTO;
    }

    @Override
    @Transactional
    public String deleteProductFromCart(Integer cartId, Integer productId) {
        Cart cart=cartRepository.findById(cartId).orElseThrow(()->new ResourceNotFoundException("Cart","CartId",cartId));
        CartItem cartItem=cartItemRepository.findCartItemByCartIdAndProductId(cartId,productId);
        if(cartItem==null){
            throw new ResourceNotFoundException("Product","ProductId",productId);
        }
        cart.setTotalPrice(cart.getTotalPrice()-(cartItem.getProductPrice()*cartItem.getQuantity()));
        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId,productId);

        return "Product "+cartItem.getProduct().getProductName()+" has been removed from the cart!!!";
    }

    @Override
    public void updateProductInCarts(Integer cartId, Integer productId) {

        Cart cart=cartRepository.findById(cartId).orElseThrow(()->new ResourceNotFoundException("Cart","CartId",cartId));
        Product product=productRepo.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product","Product Id",productId));

        CartItem cartItem=cartItemRepository.findCartItemByCartIdAndProductId(cartId,productId);
        if(cartItem==null){
            throw new APIException("Product"+  product.getProductName()+" is not available in the cart!!");
        }

        double cartPrice=cart.getTotalPrice()-(cartItem.getProductPrice()*cartItem.getQuantity());
        cartItem.setProductPrice(product.getSpecialPrice());
        cart.setTotalPrice(cartPrice+(cartItem.getProductPrice()*cartItem.getQuantity()));

        cartItemRepository.save(cartItem);



    }

    private Cart createCart(){
        Cart cart=cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(cart!=null) {
            return cart;
        }
        Cart newCart=new Cart();
        newCart.setTotalPrice(0.00);
        newCart.setUser(authUtil.loggedInUser());
        return cartRepository.save(newCart);

    }
}
