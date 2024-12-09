package com.example.back_end.service.impl;

import com.example.back_end.model.dto.order.OrderDTO;
import com.example.back_end.model.dto.order.OrderRequest;
import com.example.back_end.model.dto.orderItem.OrderItemDTO;
import jakarta.servlet.http.HttpServletRequest;
import com.example.back_end.model.dto.orderParent.OrderParentDTO;
import com.example.back_end.model.entity.*;
import com.example.back_end.repository.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderParentRepository orderParentRepository;

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
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private VNPAYService vnPayService;

    public OrderParentDTO createOrder(OrderRequest request, User user, HttpServletRequest httpServletRequest) {
        // 1. Tạo đối tượng OrderParent
        OrderParent orderParent = new OrderParent();
        orderParent.setUser(user);
        orderParent.setCreatedDate(new Date());
        orderParent.setStatus("PENDING");
        orderParent.setIsPaidBefore(request.getIsPaidBefore());
        orderParent.setPaymentType(request.getPaymentType());
        orderParent.setChildOrders(new ArrayList<>());


        Double totalOrderParent = 0.0;
        List<Long> orderedProductIds = new ArrayList<>();

        for (Map.Entry<Long, List<OrderItemDTO>> entry : groupItemsByStore(request.getItems()).entrySet()) {
            List<OrderItemDTO> items = entry.getValue();

            // Tạo Order (đơn hàng con)
            Order order = new Order();
            order.setOrderParent(orderParent);
            order.setStatus("PENDING");
            order.setCreatedDate(new Date());
            order.setAddress(
                    addressRepository.findById(request.getAddressId())
                            .orElseThrow(() -> new RuntimeException("Address not found"))
            );

            // Gán storeId bằng userId của sản phẩm đầu tiên
            Long storeId = null;
            List<OrderItem> orderItems = new ArrayList<>();
            Double totalOrder = 0.0;

            for (OrderItemDTO itemDto : items) {
                Product product = productRepository.findById(itemDto.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                // Lấy userId từ sản phẩm và gán cho storeId
                if (storeId == null) {
                    storeId = product.getUser().getId();  // Gán storeId bằng userId của sản phẩm
                }

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(product);
                orderItem.setQuantity(itemDto.getQuantity());
                orderItem.setPrice(product.getPrice());
                orderItem.setSubtotal(product.getPrice() * itemDto.getQuantity());
                orderItem.setCreatedDate(new Date());
                orderItems.add(orderItem);

                totalOrder += orderItem.getSubtotal();

                // Thêm productId vào danh sách đã đặt hàng
                orderedProductIds.add(product.getId());
            }

            order.setStoreId(storeId);  // Gán storeId cho đơn hàng
            order.setOrderItems(orderItems);
            order.setTotal(totalOrder);

            totalOrderParent += totalOrder;

            orderParent.getChildOrders().add(order);
        }

        orderParent.setTotal(totalOrderParent);

        /// Lưu OrderParent vào database
//        OrderParent savedOrderParent = orderParentRepository.save(orderParent);

        // Xóa các sản phẩm đã đặt khỏi giỏ hàng và cập nhật lại giỏ hàng
        deleteCartItems(request.getItems().stream()
                .map(OrderItemDTO::getProductId)
                .collect(Collectors.toList()), user.getId());

        // Lưu OrderParent
        orderParentRepository.save(orderParent);

        // Tạo URL thanh toán VNPAY
        String paymentUrl = vnPayService.createOrder_v2(httpServletRequest, totalOrderParent.intValue(),
                "Thanh toán đơn hàng " + orderParent.getId(),
                "http://localhost:8080/api/v1/payment", orderParent.getId().toString());

        // Trả về thông tin order + URL thanh toán
        OrderParentDTO response = mapToOrderParentDTO(orderParent);
        response.setPaymentUrl(paymentUrl);
        log.info("vnpay url: " + paymentUrl);
        return response;
    }

    @Transactional
    public void deleteCartItems(List<Long> productIds, Long userId) {
        // 1. Xóa các CartItem liên quan
        cartItemRepository.deleteCartItemsByProductIdsAndUserId(productIds, userId);

        // 2. Lấy Cart của user hiện tại
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        // 3. Tính toán lại totalItem và totalPrice
        List<CartItem> remainingItems = cart.getItems();

        int totalItem = remainingItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        double totalPrice = remainingItems.stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();

        // 4. Cập nhật Cart
        cart.setTotalItem(totalItem);
        cart.setTotalPrice(totalPrice);

        // 5. Lưu lại Cart
        cartRepository.save(cart);
    }


    private Map<Long, List<OrderItemDTO>> groupItemsByStore(List<OrderItemDTO> items) {
        // Chuyển mỗi OrderItemDTO thành một phiên bản mới có storeId từ userId của sản phẩm
        return items.stream()
                .map(item -> {
                    // Tìm sản phẩm từ productId
                    Product product = productRepository.findById(item.getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found"));

                    // Lấy userId từ sản phẩm (giả sử Product có trường user)
                    Long storeId = product.getUser().getId(); // Lấy userId từ Product

                    // Cập nhật storeId trong OrderItemDTO
                    item.setStoreId(storeId);

                    return item;
                })
                .collect(Collectors.groupingBy(OrderItemDTO::getStoreId)); // Nhóm theo storeId
    }

    private OrderParentDTO mapToOrderParentDTO(OrderParent orderParent) {
        OrderParentDTO dto = new OrderParentDTO();
        dto.setId(orderParent.getId());
        dto.setStatus(orderParent.getStatus());
        dto.setTotal(orderParent.getTotal());
        dto.setIsPaidBefore(orderParent.getIsPaidBefore());
        dto.setPaymentType(orderParent.getPaymentType());
        dto.setCreatedDate(orderParent.getCreatedDate());
        return dto;
    }

    public OrderDTO updateOrder(String request, Long id){
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));
        order.setStatus(request);
        order.setLastModifiedDate(new Date());
        return mapToOrderDTO(orderRepository.save(order));
    }

    private OrderDTO mapToOrderDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setStoreId(order.getStoreId());
        dto.setStatus(order.getStatus());
        dto.setTotal(order.getTotal());
        dto.setCreatedDate(order.getCreatedDate());
        dto.setLastModifiedDate(order.getLastModifiedDate());
        dto.setAddressId(order.getAddress().getId());
        return dto;
    }
//    public List<OrderResponse> getAllOrders() {
//        List<Order> orders = orderRepository.findAll();
//        return orders.stream().map(this::convertToOrderResponse).collect(Collectors.toList());
//    }

//    @Transactional
//    public List<OrderResponse> getOrdersByCurrentUser() {
//        try {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            User currentUser = (User) authentication.getPrincipal();
//            Long userId = currentUser.getId();
//
//            if (userId == null) {
//                throw new RuntimeException("User ID is null");
//            }
//
//            List<Order> orders = orderRepository.findByUserId(userId);
//            return orders.stream()
//                    .map(this::convertToOrderResponse)
//                    .collect(Collectors.toList());
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("Error while fetching orders for the current user: " + e.getMessage());
//        }
//    }
//
//    private OrderResponse convertToOrderResponse(Order order) {
//        OrderResponse response = new OrderResponse();
//        response.setId(order.getId());
//        response.setStatus(order.getStatus());
//        response.setNote(order.getNote());
//        response.setTotal(order.getTotal());
//        response.setIsPaidBefore(order.getIsPaidBefore());
//        response.setPaymentType(order.getPaymentType());
//        response.setTotalItem(order.getTotalItem());
//        response.setCreatedBy(order.getCreatedBy());
//        response.setLastModifiedBy(order.getLastModifiedBy());
//        response.setCreatedDate(order.getCreatedDate());
//        response.setLastModifiedDate(order.getLastModifiedDate());
//        if(order.getAddress() != null){
//            response.setAddressId(order.getAddress().getId());  // Only set address ID
//        }
//        response.setUserId(order.getUser().getId());  // Only set user ID
//
//        // Convert OrderItems to OrderItemResponses
//        List<OrderItemResponse> orderItemResponses = order.getItemList().stream()
//                .map(this::convertToOrderItemResponse)
//                .collect(Collectors.toList());
//        response.setOrderItemResponses(orderItemResponses);
//
//        return response;
//    }
//
//    private OrderItemResponse convertToOrderItemResponse(OrderItem orderItem) {
//        OrderItemResponse response = new OrderItemResponse();
//        response.setId(orderItem.getId());
//        response.setQuantity(orderItem.getQuantity());
//        response.setProductId(orderItem.getProduct().getId());  // Only set product ID
//        response.setSubtotal(orderItem.getSubtotal());
//        response.setCreatedDate(orderItem.getCreatedDate());
//        response.setLastModifiedDate(orderItem.getLastModifiedDate());
//
//        return response;
//    }

//    @Transactional
//    public String addOrder(OrderRequest orderRequest) {
//        try {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            User currentUser = (User) authentication.getPrincipal();
//            Long userId = currentUser.getId();
//
//            if (userId == null) {
//                throw new RuntimeException("User ID is null");
//            }
//
//            User user = userRepository.findById(userId)
//                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
//
//            // Xóa nhưng Order đã tạo trước đó mà chưa xác nhận đặt hàng
//            List<Order> oldOrder = orderRepository.findByUserId(userId);
//            for (Order order : oldOrder) {
//                if ("PENDING".equalsIgnoreCase(order.getStatus())) {
//                    // Xóa các OrderItem liên quan
//                    orderItemRepository.deleteByOrderId(order.getId());
//                    // Xóa Order
//                    orderRepository.delete(order);
//                }
//            }
//
//            Order order = new Order();
////            order.setNote(orderRequest.getNote());
////            order.setIsPaidBefore(orderRequest.getIsPaidBefore());
//            order.setStatus("PENDING");
//            order.setTotal(0.0);
////            order.setTotalItem(0);
////            order.setPaymentType(orderRequest.getPaymentType());
//            LocalDateTime localDateTime = LocalDateTime.now();
//            Date createdDate = ConvertToDate.convertToDateViaSqlTimestamp(localDateTime);
//            order.setCreatedDate(createdDate);
////            order.setUser(user);
//
//            Order savedOrder = orderRepository.save(order);
//
//            final double[] totalOrderAmount = {0.0};
//            final int[] totalOrderItems = {0};
//
//            List<OrderItem> items = orderRequest.getCartItems().stream().map(
//                    itemRequest -> {
//                        Product product = productService.getProductById(itemRequest.getProductId());
//
//                        System.out.println("productId: " + itemRequest.getProductId());
//                        OrderItem orderItem = new OrderItem();
//                        orderItem.setProduct(product);
//                        orderItem.setQuantity(itemRequest.getQuantity());
//                        orderItem.setPrice(product.getPromotionalPrice());
//                        orderItem.setImageUrl(product.getUrl());
//                        double subtotal = product.getPromotionalPrice() * itemRequest.getQuantity();
//                        orderItem.setSubtotal(subtotal);
//
//                        totalOrderAmount[0] += subtotal;
//                        totalOrderItems[0] += itemRequest.getQuantity();
//                        orderItem.setOrder(savedOrder);
//
//                        return orderItem;
//                    }
//            ).collect(Collectors.toList());
//
//            orderItemRepository.saveAll(items);
//
//            savedOrder.setTotal(totalOrderAmount[0]);
////            savedOrder.setTotalItem(totalOrderItems[0]);
//            orderRepository.save(savedOrder);
//
//            return "Create Order Successfully...";
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Error while creating order: " + e.getMessage();
//        }
//    }
//
//    @Transactional
//    public String confirmOrder(Long orderId, OrderRequest orderRequest) {
//        try {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            User currentUser = (User) authentication.getPrincipal();
//            Long userId = currentUser.getId();
//
//            if (userId == null) {
//                throw new RuntimeException("User ID is null");
//            }
//            Order order = orderRepository.findById(orderId)
//                    .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
//
//            Address address = addressRepository.findById(orderRequest.getIdAddress())
//                    .orElseThrow(() -> new RuntimeException("Address not found"));
//
//            if (!order.getUser().getId().equals(currentUser.getId())) {
//                return "Unauthorized to confirm this order";
//            }
//
//            if (!order.getStatus().equals("PENDING")) {
//                throw new RuntimeException("Order is not in a valid state to be confirmed");
//            }
//
//            order.setPaymentType(orderRequest.getPaymentType());
//            order.setStatus("CONFIRMED");
//            order.setAddress(address);
//            orderRepository.save(order);
//
//            List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
//
//            for (OrderItem orderItem : orderItems) {
//                Product product = orderItem.getProduct();
//                product.setQuantityAvailable(product.getQuantityAvailable() - orderItem.getQuantity());
//                product.setSold(product.getSold() == null ? orderItem.getQuantity() : product.getSold() + orderItem.getQuantity());
//                productRepository.save(product);
//            }
//
//            Cart cart = cartRepository.findByUserId(userId);
//            cart.getCartItemList().removeIf(cartItem ->
//                    orderItems.stream().anyMatch(orderItem -> orderItem.getProduct().getId().equals(cartItem.getProduct().getId())));
//
//            int totalItem = cart.getCartItemList().stream().mapToInt(CartItem::getQuantity).sum();
//            double totalPrice = cart.getCartItemList().stream().mapToDouble(item -> item.getSubtotal()).sum();
//
//// Cập nhật lại các giá trị mới cho Cart
//            cart.setTotalItem(totalItem);
//            cart.setTotalPrice(totalPrice);
//
//            cartRepository.save(cart);
//
//            return "Order Confirmed Successfully...";
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Error while confirming order: " + e.getMessage();
//        }
//    }
//    public String updateOrder(Long id, OrderRequest orderRequest){
//        try {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            User currentUser = (User) authentication.getPrincipal();
//
//            Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
//
//            // Ensure the address belongs to the current user
//            if (!order.getUser().getId().equals(currentUser.getId())) {
//                return "Unauthorized to update this address";
//            }
//
//            order.setStatus(orderRequest.getStatus());
//
//            LocalDateTime localDateTime  = LocalDateTime.now();
//            Date modifiedDate = ConvertToDate.convertToDateViaSqlTimestamp(localDateTime);
//
//            order.setLastModifiedDate(modifiedDate);
//
//            orderRepository.save(order);
//
//            return "Update Order Successfully...";
//        }catch (Exception e){
//            e.printStackTrace();
//            return "Error while update order!!!";
//        }
//    }

//    @Transactional
//    public List<OrderResponse> getOrdersByUserId(Long userId) {
//        try {
//            List<Order> orders = orderRepository.findByUserId(userId);
//            return orders.stream()
//                    .map(this::convertToOrderResponse)
//                    .collect(Collectors.toList());
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("Error while fetching orders for user ID: " + userId + ". " + e.getMessage());
//        }
//    }

//    @Transactional
//    public Optional<OrderResponse> getPendingOrdersByUserId() {
//        try {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            User currentUser = (User) authentication.getPrincipal();
//            Long userId = currentUser.getId();
//
//            List<OrderResponse> orderResponse = getOrdersByUserId(userId);
//
//            return orderResponse.stream()
//                    .filter(order -> "PENDING".equals(order.getStatus()))
//                    .findFirst();
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("Error while fetching and deleting pending orders for user " + ". " + e.getMessage());
//        }
//    }
}
