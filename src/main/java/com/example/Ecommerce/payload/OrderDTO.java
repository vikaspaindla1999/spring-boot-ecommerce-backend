package com.example.Ecommerce.payload;

import com.example.Ecommerce.model.Payment;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Integer orderId;
    private String email;
    private List<OrderItemDTO> orderItems;
    private PaymentDTO payment;
    private LocalDate orderDate;
    private Double totalAmount;
    private String orderStatus;
    private Integer addressId;
}
