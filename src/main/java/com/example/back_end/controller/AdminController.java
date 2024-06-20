package com.example.back_end.controller;

import com.example.back_end.auth.JwtService;
import com.example.back_end.config.ExtractUser;
import com.example.back_end.exception.UnauthorizedException;
import com.example.back_end.exception.UserException;
import com.example.back_end.model.dto.NotificationDTO;
import com.example.back_end.model.dto.SenderDto;
import com.example.back_end.model.entity.*;
import com.example.back_end.model.mapper.UserMapper;
import com.example.back_end.model.request.ProductRequest;
import com.example.back_end.model.request.UserRequest;
import com.example.back_end.repository.ProductRepository;
import com.example.back_end.response.ResponseObject;
import com.example.back_end.service.impl.CategoryService;
import com.example.back_end.service.impl.NotificationService;
import com.example.back_end.service.impl.ProductService;
import com.example.back_end.service.impl.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;
    private final UserMapper userMapper;
    @Autowired
    private ProductService productService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private CategoryService categoryService;

//    @GetMapping("/test")
//    public ResponseEntity<String> login(){
//        return ResponseEntity.ok("Authentication and Authorization is succedeed");
//    }

    @PostMapping("/users/create")
//    @PreAuthorize("hasRole('VENDOR')")
    public String createUser(@RequestBody UserRequest userRequest)
    {
        return userService.createUser(userRequest);
    }
    private User authenticateUser(HttpServletRequest request) throws UserException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Invalid authorization header.");
        }

        String token = authHeader.substring(7);
        ExtractUser userInfo = new ExtractUser(token, userService, jwtService);
        if (!userInfo.isEnabled()) {
            throw new UnauthorizedException("User is not enabled.");
        }

        Long userId = userInfo.getUserId();
        return userService.getUserById(Math.toIntExact(userId));
    }

    @GetMapping("/user")
    //@PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> getAllUser(){
        List<User> userList = userService.findAll();
        return ResponseEntity.ok(userMapper.toUserListDTO(userList));
    }
    @PostMapping("/product/create")
    public ResponseEntity<ResponseObject> createProduct(@RequestBody @Valid Product product, HttpServletRequest request) throws UserException {
        try {
            User user = authenticateUser(request);

            Long userId = user.getId();
            String username = user.getName();


            Long categoryId = product.getCategory().getId();
            Category category = categoryService.getCategoryById(categoryId); // Lấy thông tin Category dựa trên categoryId
            product.setCategory(category);
            product.setUser(user);
            product.setCreatedDate(new Date());
            product.setUrl(product.getUrl());
            Product createProduct = productService.createProduct(product);

            Map<String, Object> dataproduct = new LinkedHashMap<>();
            dataproduct.put("name", createProduct.getName());
            dataproduct.put("Description", createProduct.getDescription());
            dataproduct.put("price", createProduct.getPrice());
            dataproduct.put("quantity", createProduct.getQuantity());
            dataproduct.put("quantityAvailable", createProduct.getQuantityAvailable());
            dataproduct.put("categoryId", createProduct.getCategory().getId());
            dataproduct.put("UserId", createProduct.getUser().getId());
            dataproduct.put("createBy", createProduct.getUser().getName());
            dataproduct.put("createdDate", createProduct.getCreatedDate());
            dataproduct.put("isActive", Boolean.TRUE);

            ResponseObject response = ResponseObject.builder()
                    .status("Success")
                    .data(dataproduct)
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseObject.builder().message(e.getMessage()).status("ERROR").build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    public ResponseEntity<ResponseObject> createProduct(@RequestBody @Valid ProductRequest productRequest, HttpServletRequest request) throws UserException {
//        try {
//            User user = authenticateUser(request);
//            Long userId = user.getId();
//            String username = user.getName();
//
//            Category category = categoryService.getCategoryById(productRequest.getCategory().getId());
//            Product product = new Product();
//            product.setName(productRequest.getName());
//            product.setDescription(productRequest.getDescription());
//            product.setPrice(productRequest.getPrice());
//            product.setPromotionalPrice(productRequest.getPromotionalPrice());
//            product.setQuantity(productRequest.getQuantity());
//            product.setQuantityAvailable(productRequest.getQuantityAvailable());
//            product.setNumberOfRating(productRequest.getNumberOfRating());
//            product.setFavoriteCount(productRequest.getFavoriteCount());
//            product.setSold(productRequest.getSold());
//            product.setIsActive(productRequest.getIsActive());
//            product.setIsSelling(productRequest.getIsSelling());
//            product.setRating(productRequest.getRating());
//            product.setCreatedBy(username);
//            product.setCreatedDate(new Date());
//            product.setCategory(category);
//            product.setUser(user);
//            product.setImages(productRequest.getImages());
//
//            Product createdProduct = productService.createProduct(product);
//
//            // Adding images after creating the product
//            if (productRequest.getImages() != null) {
//                List<Image> images = productRequest.getImages().stream()
//                        .map(imgRequest -> {
//                            Image image = new Image();
//                            image.setUrl(imgRequest.getUrl());
//                            image.setProduct(createdProduct);
//                            return image;
//                        }).collect(Collectors.toList());
//                productService.addImagesToProduct(createdProduct, images);
//            }
//            // Fetch the updated product with images
//            Product newProduct = productService.getProductById(createdProduct.getId());
//
//            // Set the URL of the first image
//            if (newProduct.getImages() != null && !newProduct.getImages().isEmpty()) {
//                newProduct.setUrl(createdProduct.getImages().get(0).getUrl());
//                productService.createProduct(createdProduct);
//            }
//
//            Map<String, Object> dataproduct = new LinkedHashMap<>();
//            dataproduct.put("name", createdProduct.getName());
//            dataproduct.put("description", createdProduct.getDescription());
//            dataproduct.put("price", createdProduct.getPrice());
//            dataproduct.put("quantity", createdProduct.getQuantity());
//            dataproduct.put("quantityAvailable", createdProduct.getQuantityAvailable());
//            dataproduct.put("categoryId", createdProduct.getCategory().getId());
//            dataproduct.put("userId", createdProduct.getUser().getId());
//            dataproduct.put("createdBy", createdProduct.getCreatedBy());
//            dataproduct.put("createdDate", createdProduct.getCreatedDate());
//            dataproduct.put("isActive", createdProduct.getIsActive());
//            dataproduct.put("url", createdProduct.getUrl());
//
//            List<Map<String, Object>> imagesResponse = createdProduct.getImages().stream()
//                    .map(image -> {
//                        Map<String, Object> imageMap = new LinkedHashMap<>();
//                        imageMap.put("id", image.getId());
//                        imageMap.put("url", image.getUrl());
//                        return imageMap;
//                    }).collect(Collectors.toList());
//            dataproduct.put("images", imagesResponse);
//
//            ResponseObject response = ResponseObject.builder()
//                    .status("Success")
//                    .data(dataproduct)
//                    .build();
//            return ResponseEntity.status(HttpStatus.CREATED).body(response);
//        } catch (Exception e) {
//            return new ResponseEntity<>(ResponseObject.builder().message(e.getMessage()).status("ERROR").build(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
    @GetMapping("/product/my-products")
    public ResponseEntity<ResponseObject> getMyProducts(HttpServletRequest request) {
        try {
            User user = authenticateUser(request);
            Long userId = user.getId();
            List<Product> userProducts = productService.getUserProducts(userId);

            List<Map<String, Object>> productData = new ArrayList<>();
            for (Product product : userProducts) {
                Map<String, Object> dataProduct = new LinkedHashMap<>();
                dataProduct.put("id", product.getId());
                dataProduct.put("name", product.getName());
                dataProduct.put("description", product.getDescription());
                dataProduct.put("price", product.getPrice());
                dataProduct.put("quantity", product.getQuantity());
                dataProduct.put("quantityAvailable", product.getQuantityAvailable());
                dataProduct.put("category", product.getCategory().getName());
                dataProduct.put("userId", product.getUser().getId());
                dataProduct.put("createdBy", product.getUser().getName());
                dataProduct.put("createdDate", product.getCreatedDate());
                dataProduct.put("lastModifiedDate",product.getLastModifiedDate());
                dataProduct.put("isActive", product.getIsActive());
                productData.add(dataProduct);
            }

            ResponseObject response = ResponseObject.builder()
                    .status("Success")
                    .data(productData)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseObject.builder().message(e.getMessage()).status("ERROR").build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/product/{id}")
    public ResponseEntity<ResponseObject> updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct, HttpServletRequest request) {
        try {
            User user = authenticateUser(request);
            Product existingProduct = productService.getProductById(id);

            // Kiểm tra quyền của user trước khi update
            if (!existingProduct.getUser().getId().equals(user.getId())) {
                return new ResponseEntity<>(ResponseObject.builder().status("ERROR").message("You are not authorized to update this product.").build(), HttpStatus.FORBIDDEN);
            }

            // Cập nhật thông tin sản phẩm
            existingProduct.setName(updatedProduct.getName());
            existingProduct.setDescription(updatedProduct.getDescription());
            existingProduct.setPrice(updatedProduct.getPrice());
            existingProduct.setQuantity(updatedProduct.getQuantity());
            existingProduct.setQuantityAvailable(updatedProduct.getQuantityAvailable());
            existingProduct.setCategory(updatedProduct.getCategory());
            existingProduct.setActive(updatedProduct.getIsActive());
            existingProduct.setLastModifiedDate(new Date());

            Product updatedProductEntity = productService.updateProduct(existingProduct);

            Map<String, Object> dataProduct = new LinkedHashMap<>();
            dataProduct.put("id", updatedProductEntity.getId());
            dataProduct.put("name", updatedProductEntity.getName());
            dataProduct.put("description", updatedProductEntity.getDescription());
            dataProduct.put("price", updatedProductEntity.getPrice());
            dataProduct.put("quantity", updatedProductEntity.getQuantity());
            dataProduct.put("quantityAvailable", updatedProductEntity.getQuantityAvailable());
            dataProduct.put("categoryId", updatedProductEntity.getCategory().getId());
            dataProduct.put("userId", updatedProductEntity.getUser().getId());
            dataProduct.put("createdBy", updatedProductEntity.getUser().getName());
            dataProduct.put("createdDate", updatedProductEntity.getCreatedDate());
            dataProduct.put("isActive",updatedProduct.getIsActive());

            ResponseObject response = ResponseObject.builder().status("Success").data(dataProduct).build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseObject.builder().message(e.getMessage()).status("ERROR").build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/product/{productID}")
    ResponseEntity<ResponseObject> getDetailProduct(@PathVariable("productID")Integer id){
        try{
            Product product = productService.getSelectedProduct(id);
            if(product == null){
                return new ResponseEntity<ResponseObject>(ResponseObject.builder().status("ERROR").message("Position not found").build(),HttpStatus.OK);
            }
            return new ResponseEntity<ResponseObject>(ResponseObject.builder().status("SUCCESS").data(product).build(),HttpStatus.OK);

        }catch (Exception exception){
            return new ResponseEntity<ResponseObject>(ResponseObject.builder().status("ERROR").message(exception.getMessage()).build(),HttpStatus.OK);
        }
    }
    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationDTO>> getNotifications(HttpServletRequest request) {
        try {
            User user = authenticateUser(request);
            List<Notification> notifications = notificationService.getNotificationsByUser(user);
            List<NotificationDTO> notificationDtos = notifications.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(notificationDtos);
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    @GetMapping("/notifications/{id}")
    public ResponseEntity<NotificationDTO> getNotificationDetails(@PathVariable Long id, HttpServletRequest request) {
        try {
            User user = authenticateUser(request);
            Notification notification = notificationService.getNotificationById(id);
            if (notification != null && notification.getRecipient().equals(user)) {
                notification.setRead(true);
                notificationService.saveNotification(notification);
                NotificationDTO dto = convertToDto(notification);
                return ResponseEntity.ok(dto);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private NotificationDTO convertToDto(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setSender(new SenderDto(notification.getSender().getId(), notification.getSender().getName()));
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setId(notification.getId());
        dto.setRead(notification.isRead());
        return dto;
    }

    @GetMapping("/notifications/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(HttpServletRequest request) {
        try {
            User user = authenticateUser(request);
            List<Notification> unreadNotifications = notificationService.getUnreadNotificationsByUser(user);
            List<NotificationDTO> notificationDtos = unreadNotifications.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(notificationDtos);
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/products/total-revenue")
    public ResponseEntity<Map<String, Double>> getTotalRevenue(HttpServletRequest request) throws UserException {
        User user = authenticateUser(request);
        Double totalRevenue = productService.getTotalRevenueByUser(user.getId());
        Map<String, Double> response = new HashMap<>();
        response.put("totalRevenue", totalRevenue);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/products/total-sold")
    public ResponseEntity<Map<String, Long>> getTotalProductQuantity(HttpServletRequest request) throws UserException {
        User user = authenticateUser(request);
        Long totalSold = productService.getTotalProductSoldByUser(user.getId());
        Map<String, Long> response = new HashMap<>();
        response.put("totalSold", totalSold);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/products/revenue")
    public ResponseEntity<Map<String, Double>> getRevenue(HttpServletRequest request) throws UserException {
        User user = authenticateUser(request);
        Double Revenue = productService.getRevenueByUser(user.getId());
        Map<String, Double> response = new HashMap<>();
        response.put("Revenue", Revenue);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/products/sold")
    public ResponseEntity<Map<String, Long>> getProductQuantity(HttpServletRequest request) throws UserException {
        User user = authenticateUser(request);
        Long sold = productService.getProductSoldByUser(user.getId());
        Map<String, Long> response = new HashMap<>();
        response.put("Sold", sold);
        return ResponseEntity.ok(response);
    }
}
