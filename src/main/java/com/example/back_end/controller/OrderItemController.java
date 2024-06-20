package com.example.back_end.controller;

import com.example.back_end.model.entity.Order;
import com.example.back_end.model.entity.OrderItem;
import com.example.back_end.model.response.OrderItemResponse;
import com.example.back_end.repository.OrderItemRepository;
import com.example.back_end.service.impl.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orderItem")
public class OrderItemController {
    @Autowired
    private OrderItemService orderItemService;
    @GetMapping("/getAll")
//    public ResponseEntity<List<OrderItem>> getAllOrders() {
//        List<OrderItem> orderItems = orderItemService.getAllOrders();
//        return ResponseEntity.ok(orderItems);
//    }
    public ResponseEntity<List<OrderItemResponse>> getAllOrderItems() {
        List<OrderItemResponse> orderItemResponses = orderItemService.getAllOrderItems();
        return ResponseEntity.ok(orderItemResponses);
    }
}
