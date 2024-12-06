package com.example.back_end.controller;

import com.example.back_end.model.dto.order.OrderDTO;
import com.example.back_end.model.dto.order.OrderRequest;
import com.example.back_end.model.dto.orderParent.OrderParentDTO;
import com.example.back_end.model.entity.*;
import com.example.back_end.model.request.OrderItemRequest;
import com.example.back_end.model.response.CartItemResponse;
import com.example.back_end.model.response.OrderResponse;
import com.example.back_end.repository.OrderParentRepository;
import com.example.back_end.repository.ProductRepository;
import com.example.back_end.service.impl.OrderService;
import com.example.back_end.service.impl.ProductService;
import com.example.back_end.service.impl.VNPAYService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderParentRepository orderParentRepository;

    @Autowired
    private VNPAYService vnpayService;
    @PostMapping("/createOrder")
    public ResponseEntity<OrderParentDTO> createOrder(@RequestBody OrderRequest request, HttpServletRequest httpServletRequest) {
        User user = getUser(); // Xử lý lấy user từ token hoặc session
        OrderParentDTO orderParentDTO = orderService.createOrder(request, user, httpServletRequest);
        return ResponseEntity.ok(orderParentDTO);
    }

    @PostMapping("/payment/return")
    public ResponseEntity<String> handleVnpayReturn(HttpServletRequest request) {
        int result = vnpayService.orderReturn(request);

        if (result == 1) {
            // Thành công, cập nhật trạng thái đơn hàng
            String txnRef = request.getParameter("vnp_TxnRef");
            OrderParent orderParent = orderParentRepository.findById(Long.valueOf(txnRef))
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            orderParent.setStatus("PAID");
            orderParentRepository.save(orderParent);

            return ResponseEntity.ok("Thanh toán thành công");
        } else if (result == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Thanh toán thất bại");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Chữ ký không hợp lệ");
        }
    }

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return user;
    }

    @PreAuthorize("hasRole('VENDOR')")
    @PutMapping("/updateOrder/{id}")
    public ResponseEntity<OrderDTO> updateOrder(@RequestParam("status") String status, @PathVariable Long id) {
        OrderDTO orderDTO = orderService.updateOrder(status, id);
        return ResponseEntity.ok(orderDTO);
    }

    @PutMapping("/cancelOrder/{id}")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable Long id) {
        String status = "CANCEL";
        OrderDTO orderDTO = orderService.updateOrder(status, id);
        return ResponseEntity.ok(orderDTO);
    }

//    @GetMapping("/getAll")
//    public ResponseEntity<List<OrderResponse>> getAllOrders() {
//        List<OrderResponse> orderResponses = orderService.getAllOrders();
//        return ResponseEntity.ok(orderResponses);
//    }
//
//    @GetMapping("/my-orders")
//    public ResponseEntity<List<OrderResponse>> getOrdersByCurrentUser() {
//        List<OrderResponse> orders = orderService.getOrdersByCurrentUser();
//        return ResponseEntity.ok(orders);
//    }
//
//    @PostMapping("/addOrder")
//    public ResponseEntity<String> addOrder(@RequestBody OrderRequest orderRequest) {
//        try {
//            String result = orderService.addOrder(orderRequest);
//            if (result.equals("Create Order Successfully...")) {
//                return ResponseEntity.ok(result);
//            } else {
//                return ResponseEntity.status(500).body(result);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("Error while creating order: " + e.getMessage());
//        }
//    }
//
//    @PostMapping("/confirmOrder/{orderId}")
//    public ResponseEntity<String> confirmOrder(@PathVariable Long orderId, @RequestBody OrderRequest orderRequest) {
//        try {
//            System.out.println("paymentType: " + orderRequest.getPaymentType());
//            String result = orderService.confirmOrder(orderId, orderRequest);
//            if (result.equals("Order Confirmed Successfully...")) {
//                return ResponseEntity.ok(result);
//            } else {
//                return ResponseEntity.status(500).body(result);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("Error while confirming order: " + e.getMessage());
//        }
//    }
//
//    @PutMapping("/updateOrder/{id}")
//    public ResponseEntity<String> updateOrder(@PathVariable Long id, @RequestBody OrderRequest orderRequest){
//        String result = orderService.updateOrder(id,orderRequest);
//        if (result.equals("Update Address Successfully...")) {
//            return ResponseEntity.ok(result);
//        } else {
//            return ResponseEntity.status(500).body(result);
//        }
//    }
//
//    @GetMapping("/pending")
//    public ResponseEntity<Object> getPendingOrderForUser() {
//        try {
//            Optional<OrderResponse> pendingOrderOptional = orderService.getPendingOrdersByUserId();
//            if (pendingOrderOptional.isPresent()) {
//                return ResponseEntity.ok(pendingOrderOptional.get());
//            } else {
//                return ResponseEntity.notFound().build();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while getting pending order: " + e.getMessage());
//        }
//    }
}
