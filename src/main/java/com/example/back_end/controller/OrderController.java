package com.example.back_end.controller;

import com.example.back_end.model.entity.Order;
import com.example.back_end.model.entity.OrderItem;
import com.example.back_end.model.entity.Product;
import com.example.back_end.model.request.OrderItemRequest;
import com.example.back_end.model.request.OrderRequest;
import com.example.back_end.model.response.CartItemResponse;
import com.example.back_end.model.response.OrderResponse;
import com.example.back_end.repository.ProductRepository;
import com.example.back_end.service.impl.OrderService;
import com.example.back_end.service.impl.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

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
    public ResponseEntity<String> addOrder(@RequestBody OrderRequest orderRequest) {
        try {
            String result = orderService.addOrder(orderRequest);
            if (result.equals("Create Order Successfully...")) {
//                // Cập nhật lại số lượng sản phẩm và số lượng đã bán
//                for (OrderItemRequest item : orderRequest.getOrderItems()) {
//                    Product product = productService.getProductById(item.getProduct().getId());
//                    product.setQuantityAvailable(product.getQuantityAvailable() - item.getQuantity());
//                    product.setSold(product.getSold() == null ? item.getQuantity() : product.getSold() + item.getQuantity());
//                    productRepository.save(product);
//                }
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(500).body(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error while creating order: " + e.getMessage());
        }
    }

    @PostMapping("/confirmOrder/{orderId}")
    public ResponseEntity<String> confirmOrder(@PathVariable Long orderId, @RequestBody OrderRequest orderRequest) {
        try {
            System.out.println("paymentType: " + orderRequest.getPaymentType());
            String result = orderService.confirmOrder(orderId, orderRequest);
            if (result.equals("Order Confirmed Successfully...")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(500).body(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error while confirming order: " + e.getMessage());
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

    @GetMapping("/pending")
    public ResponseEntity<Object> getPendingOrderForUser() {
        try {
            Optional<OrderResponse> pendingOrderOptional = orderService.getPendingOrdersByUserId();
            if (pendingOrderOptional.isPresent()) {
                return ResponseEntity.ok(pendingOrderOptional.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while getting pending order: " + e.getMessage());
        }
    }
}
