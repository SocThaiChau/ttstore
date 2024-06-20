package com.example.back_end.service.impl;

import com.example.back_end.model.entity.Order;
import com.example.back_end.model.entity.OrderItem;
import com.example.back_end.model.entity.Product;
import com.example.back_end.model.response.OrderItemResponse;
import com.example.back_end.model.response.OrderResponse;
import com.example.back_end.model.response.ProductResponse;
import com.example.back_end.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderItemService {
    @Autowired
    private OrderItemRepository orderItemRepository;

    public List<OrderItemResponse> getAllOrderItems() {
        List<OrderItem> orderItems = orderItemRepository.findAll();
        return orderItems.stream().map(this::convertToOrderItemResponse).collect(Collectors.toList());
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

        // Set product responses
        ProductResponse productResponse = convertToProductResponse(orderItem.getProduct());
        response.setProductResponse(productResponse);

        // Set order response
        Order order = orderItem.getOrder();
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setId(order.getId());
        orderResponse.setStatus(order.getStatus());
        orderResponse.setNote(order.getNote());
        orderResponse.setTotal(order.getTotal());
        orderResponse.setIsPaidBefore(order.getIsPaidBefore());
        orderResponse.setPaymentType(order.getPaymentType());
        orderResponse.setTotalItem(order.getTotalItem());
        orderResponse.setCreatedDate(order.getCreatedDate());
        orderResponse.setLastModifiedBy(order.getLastModifiedBy());
        orderResponse.setAddress(order.getAddress());
        orderResponse.setUser(order.getUser());
        response.setOrderResponse(orderResponse);

        return response;
    }

    private ProductResponse convertToProductResponse(Product product) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(product.getId());
        productResponse.setName(product.getName());
        productResponse.setDescription(product.getDescription());
        productResponse.setPrice(product.getPrice());
        productResponse.setPromotionalPrice(product.getPromotionalPrice());
        productResponse.setQuantity(product.getQuantity());
        productResponse.setQuantityAvailable(product.getQuantityAvailable());
        productResponse.setNumberOfRating(product.getNumberOfRating());
        productResponse.setFavoriteCount(product.getFavoriteCount());
        productResponse.setSold(product.getSold());
        productResponse.setIsActive(product.getIsActive());
        productResponse.setIsSelling(product.getIsSelling());
        productResponse.setRating(product.getRating());
        productResponse.setCreatedBy(product.getCreatedBy());
        productResponse.setLastModifiedBy(product.getLastModifiedBy());
        productResponse.setCreatedDate(product.getCreatedDate());
        productResponse.setLastModifiedDate(product.getLastModifiedDate());
        productResponse.setImages(product.getImages());
        productResponse.setProductReviewList(product.getProductReviewList());

        return productResponse;
    }

}
