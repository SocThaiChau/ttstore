package com.example.back_end.service.impl;

import com.example.back_end.config.ConvertToDate;
import com.example.back_end.model.entity.*;
import com.example.back_end.model.request.OrderItemRequest;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
        response.setAddress(order.getAddress());
        response.setUser(order.getUser());

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
        response.setPrice(orderItem.getPrice());
        response.setSubtotal(orderItem.getSubtotal());
        response.setImageUrl(orderItem.getImageUrl());
        response.setCreatedDate(orderItem.getCreatedDate());
        response.setLastModifiedDate(orderItem.getLastModifiedDate());

        Product product = orderItem.getProduct();
        if (product != null) {
            ProductResponse productResponse = new ProductResponse();
            productResponse.setId(product.getId());
            productResponse.setName(product.getName());
            productResponse.setDescription(product.getDescription());
            productResponse.setImages(product.getImages());
            productResponse.setCreatedDate(product.getCreatedDate());
            productResponse.setFavoriteCount(product.getFavoriteCount());
            productResponse.setIsActive(product.getIsActive());
            productResponse.setIsSelling(product.getIsSelling());
            productResponse.setSold(product.getSold());
            productResponse.setRating(product.getRating());
            productResponse.setProductReviewList(product.getProductReviewList());
            productResponse.setPromotionalPrice(product.getPromotionalPrice());
            productResponse.setPrice(product.getPrice());
            productResponse.setQuantityAvailable(product.getQuantityAvailable());
            productResponse.setQuantity(product.getQuantity());
            productResponse.setNumberOfRating(product.getNumberOfRating());
            productResponse.setLastModifiedBy(product.getLastModifiedBy());
            productResponse.setLastModifiedDate(product.getLastModifiedDate());

            response.setProductResponse(productResponse);
        }

        OrderResponse orderResponse = new OrderResponse();
        if (orderItem.getOrder() != null) {
            orderResponse.setId(orderItem.getOrder().getId());
            orderResponse.setStatus(orderItem.getOrder().getStatus());
            orderResponse.setNote(orderItem.getOrder().getNote());
            orderResponse.setTotal(orderItem.getOrder().getTotal());
            orderResponse.setIsPaidBefore(orderItem.getOrder().getIsPaidBefore());
            orderResponse.setPaymentType(orderItem.getOrder().getPaymentType());
            orderResponse.setTotalItem(orderItem.getOrder().getTotalItem());
            orderResponse.setCreatedDate(orderItem.getOrder().getCreatedDate());
            orderResponse.setLastModifiedBy(orderItem.getOrder().getLastModifiedBy());
            orderResponse.setAddress(orderItem.getOrder().getAddress());
            orderResponse.setUser(orderItem.getOrder().getUser());
        }
        response.setOrderResponse(orderResponse);

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
            order.setNote(orderRequest.getNote());
            order.setIsPaidBefore(orderRequest.getIsPaidBefore());
            order.setStatus(orderRequest.getStatus());
            order.setTotal(orderRequest.getTotal());
            order.setTotalItem(orderRequest.getTotalItem());
            order.setPaymentType(orderRequest.getPaymentType());
            LocalDateTime localDateTime = LocalDateTime.now();
            Date createdDate = ConvertToDate.convertToDateViaSqlTimestamp(localDateTime);
            order.setCreatedDate(createdDate);
            order.setUser(user);
            order.setAddress(address);

            // Save order to get its ID
            Order savedOrder = orderRepository.save(order);

            // Create order items list<OrderItem>
            List<OrderItem> items = orderRequest.getOrderItems().stream().map(
                    item -> {
                        Product product = productService.getProductById(item.getProduct().getId());

                        OrderItem orderItem = new OrderItem();
                        orderItem.setPrice(item.getProduct().getPrice());
                        orderItem.setQuantity(item.getQuantity());
                        orderItem.setSubtotal(item.getProduct().getPrice() * item.getQuantity());
//                        orderItem.setImageUrl(item.getProduct().getImages().toString());
                        orderItem.setProduct(product);
                        orderItem.setOrder(savedOrder); // Set order for the item

                        return orderItem;
                    }
            ).toList();

            // Save each order item
            orderItemRepository.saveAll(items);

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
