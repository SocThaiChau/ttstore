package com.example.back_end.controller;

import com.example.back_end.auth.JwtService;
import com.example.back_end.config.ExtractUser;
import com.example.back_end.exception.UnauthorizedException;
import com.example.back_end.exception.UserException;
import com.example.back_end.model.dto.NotificationDTO;
import com.example.back_end.model.dto.SenderDto;
import com.example.back_end.model.dto.user.UserDTO;
import com.example.back_end.model.entity.*;
import com.example.back_end.model.mapper.UserMapper;
import com.example.back_end.model.request.ProductRequest;
import com.example.back_end.model.request.UserRequest;
import com.example.back_end.model.response.CategoryResponse;
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
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> getAllUser(){
        List<User> userList = userService.findAll();
        return ResponseEntity.ok(userMapper.toUserListDTO(userList));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> getAllUsers(){
        List<UserDTO> userList = userService.findAllUser();
        return ResponseEntity.ok(userList);
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


}
