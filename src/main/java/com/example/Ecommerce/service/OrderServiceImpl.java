package com.example.Ecommerce.service;

import com.example.Ecommerce.exceptions.APIException;
import com.example.Ecommerce.exceptions.ResourceNotFoundException;
import com.example.Ecommerce.model.*;
import com.example.Ecommerce.payload.OrderDTO;
import com.example.Ecommerce.payload.OrderItemDTO;
import com.example.Ecommerce.payload.ProductDTO;
import com.example.Ecommerce.repository.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private CartService cartService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderDTO placeOrder(String emailId, String paymentMethod, String pgPaymentId, Integer addressId, String pgName, String pgStatus, String pgResponseMessage) {
        Cart cart=cartRepository.findCartByEmail(emailId);
        if(cart==null){
            throw new ResourceNotFoundException("Cart","EmailId",emailId);
        }
        Address address=addressRepository.findById(addressId)
                .orElseThrow(()->new ResourceNotFoundException("Address","AddressId",addressId));

        Order order=new Order();
        order.setEmail(emailId);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Order Accepted!!");
        order.setAddress(address);

        Payment payment=new Payment(paymentMethod,pgPaymentId,pgStatus,pgResponseMessage,pgName);
        payment.setOrder(order);
        Payment savedPayment=paymentRepository.save(payment);
        order.setPayment(savedPayment);
        Order savedOrder=orderRepository.save(order);

        List<CartItem> cartItems=cart.getCartItems();
        if(cartItems.isEmpty()){
            throw new APIException("Cart is Empty!!");
        }

        List<OrderItem> orderItems=new ArrayList<>();
        for(CartItem cartItem:cartItems){
            OrderItem orderItem=new OrderItem();
            orderItem.setOrder(order);
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItems.add(orderItem);
        }

       List<OrderItem> orderedItems= orderItemRepository.saveAll(orderItems);
        cartItems.forEach(item->{
            int quantity=item.getQuantity();
            Product product=item.getProduct();
            product.setQuantity(product.getQuantity()-quantity);
            productRepo.save(product);
            cartService.deleteProductFromCart(cart.getCartId(),item.getProduct().getProductId());
        });
        OrderDTO orderDTO=modelMapper.map(savedOrder,OrderDTO.class);
        orderedItems.forEach(item->{
            orderDTO.getOrderItems().add(
            modelMapper.map(item, OrderItemDTO.class));
        });

        orderDTO.setAddressId(addressId);

        return orderDTO;
    }
}
