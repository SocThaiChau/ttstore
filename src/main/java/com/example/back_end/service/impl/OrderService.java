package com.example.back_end.service.impl;

import com.example.back_end.config.ConvertToDate;
import com.example.back_end.model.entity.*;
import com.example.back_end.model.request.OrderRequest;
import com.example.back_end.model.response.OrderItemResponse;
import com.example.back_end.model.response.OrderResponse;
import com.example.back_end.model.response.ProductResponse;
import com.example.back_end.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductService productService;

    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::convertToOrderResponse).collect(Collectors.toList());
    }

    @Transactional
    public List<OrderResponse> getOrdersByCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();
            Long userId = currentUser.getId();

            if (userId == null) {
                throw new RuntimeException("User ID is null");
            }

            List<Order> orders = orderRepository.findByUserId(userId);
            return orders.stream()
                    .map(this::convertToOrderResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while fetching orders for the current user: " + e.getMessage());
        }
    }

    private OrderResponse convertToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setStatus(order.getStatus());
        response.setNote(order.getNote());
        response.setTotal(order.getTotal());
        response.setIsPaidBefore(order.getIsPaidBefore());
        response.setPaymentType(order.getPaymentType());
        response.setTotalItem(order.getTotalItem());
        response.setCreatedBy(order.getCreatedBy());
        response.setLastModifiedBy(order.getLastModifiedBy());
        response.setCreatedDate(order.getCreatedDate());
        response.setLastModifiedDate(order.getLastModifiedDate());
        response.setAddressId(order.getAddress().getId());  // Only set address ID
        response.setUserId(order.getUser().getId());  // Only set user ID

        // Convert OrderItems to OrderItemResponses
        List<OrderItemResponse> orderItemResponses = order.getItemList().stream()
                .map(this::convertToOrderItemResponse)
                .collect(Collectors.toList());
        response.setOrderItemResponses(orderItemResponses);

        return response;
    }

    private OrderItemResponse convertToOrderItemResponse(OrderItem orderItem) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(orderItem.getId());
        response.setQuantity(orderItem.getQuantity());
        response.setProductId(orderItem.getProduct().getId());  // Only set product ID
        response.setSubtotal(orderItem.getSubtotal());
        response.setCreatedDate(orderItem.getCreatedDate());
        response.setLastModifiedDate(orderItem.getLastModifiedDate());

        return response;
    }

    @Transactional
    public String addOrder(OrderRequest orderRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();
            Long userId = currentUser.getId();
            if (userId == null) {
                throw new RuntimeException("User ID is null");
            }

            // Find user from UserRepository
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

            Address address = addressRepository.findById(orderRequest.getIdAddress())
                    .orElseThrow(() -> new RuntimeException("Address not found"));

            // Create order and set its properties
            Order order = new Order();
//            order.setNote(orderRequest.getNote());
            order.setIsPaidBefore(false);
            order.setStatus("Đang chờ xác nhận");
            order.setTotal(0.0); // Initialize total to 0.0
            order.setTotalItem(0);
            order.setPaymentType("Thanh toán khi nhận hàng");
            LocalDateTime localDateTime = LocalDateTime.now();
            Date createdDate = ConvertToDate.convertToDateViaSqlTimestamp(localDateTime);
            order.setCreatedDate(createdDate);
            order.setUser(user);
            order.setAddress(address);

            // Save order to get its ID
            Order savedOrder = orderRepository.save(order);

            final double[] totalOrderAmount = {0.0}; // Use array to hold the total amount
            final int[] totalOrderItems = {0}; // Use array to hold the total item count

            // Create order items list<OrderItem>
            List<OrderItem> items = orderRequest.getOrderItems().stream().map(
                    item -> {
                        Product product = productService.getProductById(item.getProductId());
                        if (product == null) {
                            throw new RuntimeException("Product not found with ID: " + item.getProductId());
                        }

                        OrderItem orderItem = new OrderItem();
                        orderItem.setPrice(product.getPrice());
                        orderItem.setQuantity(item.getQuantity());
                        double subtotal = product.getPrice() * item.getQuantity();
                        orderItem.setSubtotal(subtotal);
                        totalOrderAmount[0] += subtotal; // Add subtotal to totalOrderAmount
                        totalOrderItems[0] += item.getQuantity(); // Add item quantity to totalOrderItems
                        orderItem.setProduct(product);
                        orderItem.setOrder(savedOrder); // Set order for the item

                        return orderItem;
                    }
            ).toList();

            // Save each order item
            orderItemRepository.saveAll(items);

            // Update order with total amount and total items
            savedOrder.setTotal(totalOrderAmount[0]);
            savedOrder.setTotalItem(totalOrderItems[0]);
            orderRepository.save(savedOrder); // Save updated order

            return "Create Order Successfully...";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error while creating order: " + e.getMessage();
        }
    }

    public String updateOrder(Long id, OrderRequest orderRequest){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();

            Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));

            // Ensure the address belongs to the current user
            if (!order.getUser().getId().equals(currentUser.getId())) {
                return "Unauthorized to update this address";
            }

            order.setStatus(orderRequest.getStatus());

            LocalDateTime localDateTime  = LocalDateTime.now();
            Date modifiedDate = ConvertToDate.convertToDateViaSqlTimestamp(localDateTime);

            order.setLastModifiedDate(modifiedDate);

            orderRepository.save(order);

            return "Update Order Successfully...";
        }catch (Exception e){
            e.printStackTrace();
            return "Error while update order!!!";
        }
    }
}
