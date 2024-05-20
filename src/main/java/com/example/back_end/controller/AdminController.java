package com.example.back_end.controller;

import com.example.back_end.model.entity.User;
import com.example.back_end.model.mapper.UserMapper;
import com.example.back_end.model.request.UserRequest;
import com.example.back_end.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;
    private final UserMapper userMapper;

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


    @GetMapping("/user")
//    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> getAllUser(){
        List<User> userList = userService.findAll();
        return ResponseEntity.ok(userMapper.toUserListDTO(userList));
    }
}
