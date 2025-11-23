package com.example.Ecommerce.controller;

import com.example.Ecommerce.payload.OrderDTO;
import com.example.Ecommerce.payload.OrderRequestDTO;
import com.example.Ecommerce.service.OrderService;
import com.example.Ecommerce.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {
  @Autowired
  private OrderService orderService;

  @Autowired
  private AuthUtil authUtil;
  @Tag(name = "Order APIs",description = "API for managing Orders")
  @Operation(summary = "Place Order",description = "API to place order")
  @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> orderProducts(@PathVariable String paymentMethod, @RequestBody OrderRequestDTO orderRequestDTO) {
      String emailId = authUtil.loggedInEmail();
      OrderDTO order = orderService.placeOrder(
              emailId,
              paymentMethod,
              orderRequestDTO.getPgPaymentId(),
              orderRequestDTO.getAddressId(),
              orderRequestDTO.getPgName(),
              orderRequestDTO.getPgStatus(),
              orderRequestDTO.getPgResponseMessage());

      return new ResponseEntity<OrderDTO>(order, HttpStatus.CREATED);
  }
}
