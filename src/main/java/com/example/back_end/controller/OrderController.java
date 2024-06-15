package com.example.back_end.controller;

import com.example.back_end.model.entity.Order;
import com.example.back_end.model.request.OrderRequest;
import com.example.back_end.model.response.OrderResponse;
import com.example.back_end.service.impl.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/getAll")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orderResponses = orderService.getAllOrders();
        return ResponseEntity.ok(orderResponses);
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponse>> getOrdersByCurrentUser() {
        List<OrderResponse> orders = orderService.getOrdersByCurrentUser();
        return ResponseEntity.ok(orders);
    }


    @PostMapping("/addOrder")
    public ResponseEntity<String> addOrder(@RequestBody OrderRequest orderRequest){
        String result = orderService.addOrder(orderRequest);
        if (result.equals("Create Address Successfully...")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(500).body(result);
        }
    }

    @PutMapping("/updateOrder/{id}")
    public ResponseEntity<String> updateOrder(@PathVariable Long id, @RequestBody OrderRequest orderRequest){
        String result = orderService.updateOrder(id,orderRequest);
        if (result.equals("Update Address Successfully...")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(500).body(result);
        }
    }
}
