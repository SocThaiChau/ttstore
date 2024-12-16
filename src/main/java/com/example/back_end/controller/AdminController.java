package com.example.back_end.controller;

import com.example.back_end.auth.JwtService;
import com.example.back_end.config.ExtractUser;
import com.example.back_end.exception.ErrorResponse;
import com.example.back_end.exception.ProductException;
import com.example.back_end.exception.UnauthorizedException;
import com.example.back_end.exception.UserException;
import com.example.back_end.model.dto.NotificationDTO;
import com.example.back_end.model.dto.SalesDTO;
import com.example.back_end.model.dto.SenderDto;
import com.example.back_end.model.dto.user.UserDTO;
import com.example.back_end.model.entity.*;
import com.example.back_end.model.mapper.UserMapper;
import com.example.back_end.model.request.ProductRequest;
import com.example.back_end.model.request.UserRequest;
import com.example.back_end.model.response.CategoryResponse;
import com.example.back_end.repository.ProductRepository;
import com.example.back_end.repository.UserRepository;
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
import org.springframework.security.access.prepost.PreAuthorize;
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
    @Autowired
    private UserRepository userRepository;


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

    @GetMapping("/users")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers(){
        List<UserDTO> userList = userService.findAllUser();
        return ResponseEntity.ok(userList);
    }
    @PutMapping("/users/{id}/update")
// @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest userRequest
    ) {
        try {
            // Kiểm tra xem user có tồn tại hay không
            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new UserException("User not found"));

            // Cập nhật thông tin từ request
            if (userRequest.getName() != null) existingUser.setName(userRequest.getName());
            if (userRequest.getEmail() != null) existingUser.setEmail(userRequest.getEmail());
            if (userRequest.getPhoneNumber() != null) existingUser.setPhoneNumber(userRequest.getPhoneNumber());
            if (userRequest.getGender() != null) existingUser.setGender(userRequest.getGender());
            if (userRequest.getAddress() != null) existingUser.setAddress(userRequest.getAddress());
            if (userRequest.getDob() != null) existingUser.setDob(userRequest.getDob());

            // Lưu các thay đổi vào cơ sở dữ liệu
            existingUser.setLastModifiedDate(new Date());
            userRepository.save(existingUser);

            // Chuyển đổi sang UserDTO để trả về phản hồi
            UserDTO updatedUserDTO = userMapper.toUserDTO(existingUser);
            return ResponseEntity.ok(new ResponseObject("success", "User updated successfully", updatedUserDTO));

        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject("error", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("error", "An unexpected error occurred", null));
        }
    }
    @GetMapping("/users/{id}")
// @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserDetails(@PathVariable Long id) {
        try {
            // Tìm kiếm người dùng theo ID
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new UserException("User not found"));

            // Chuyển đổi entity sang DTO
            UserDTO userDTO = userMapper.toUserDTO(user);

            // Trả về phản hồi
            return ResponseEntity.ok(new ResponseObject("success", "User details retrieved successfully", userDTO));
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject("error", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("error", "An unexpected error occurred", null));
        }
    }



    @GetMapping("/category/{id}")
    //@PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> getCategory(@PathVariable Long id){
        Category category = userService.findCategoryById(id);

        if (category == null) {
            return ResponseEntity.status(404).body("Category not found");
        }

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setId(category.getId());
        categoryResponse.setName(category.getName());
        categoryResponse.setImage(category.getImage());
        // Thêm các trường khác nếu cần

        return ResponseEntity.ok(categoryResponse);
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
//    @GetMapping("/products/sold-products")
//    public ResponseEntity<Integer> getTotalSoldProducts( HttpServletRequest request) {
//        try {
//            User user = authenticateUser(request);
//            int totalSoldProducts = productService.getTotalSoldProductsByUser(user.getId());
//            //String response = "Tổng số lượng sản phẩm đã bán được: " + totalSoldProducts;
//            return ResponseEntity.ok(totalSoldProducts);
//        } catch (UserException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//    }
    @GetMapping("/user/sales")
    public ResponseEntity<Map<String, Object>> getTotalProductSales(HttpServletRequest request) throws UserException {
        User user = authenticateUser(request);
        Long userId = user.getId();
        int totalSales = productService.getTotalSoldProductsByUser(userId);
        double totalRevenue = productService.getTotalRevenueByUser(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("totalSales", totalSales);
        response.put("totalRevenue", totalRevenue);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/sales/{productId}")
    public ResponseEntity<?> getSalesByUserAndProduct(@PathVariable Long productId, HttpServletRequest request) {
        try {
            User user = authenticateUser(request);
            SalesDTO salesDTO = productService.getSalesByUserAndProduct(user.getId(), productId);
            return ResponseEntity.ok(salesDTO);
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (ProductException e) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .message("Product not found")
                    .description(e.getMessage())
                    .timestamp(new Date())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
    @GetMapping("/sales")
    public ResponseEntity<List<SalesDTO>> getSalesByUser(HttpServletRequest request) {
        try {
            User user = authenticateUser(request);
            List<SalesDTO> salesDTOs = productService.getSalesByUser(user.getId());
            return ResponseEntity.ok(salesDTOs);
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
