package com.example.Ecommerce.service;

import com.example.Ecommerce.payload.OrderDTO;

public interface OrderService {
    OrderDTO placeOrder(String emailId, String paymentMethod, String pgPaymentId, Integer addressId, String pgName, String pgStatus, String pgResponseMessage);
}
