package com.example.back_end.controller;

import com.example.back_end.auth.JwtService;
import com.example.back_end.config.ExtractUser;
import com.example.back_end.exception.UserException;
import com.example.back_end.model.entity.Product;
import com.example.back_end.model.entity.User;
import com.example.back_end.model.request.UserRequest;
import com.example.back_end.response.ResponseObject;
import com.example.back_end.service.impl.ProductService;
import com.example.back_end.service.impl.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;

    @Autowired
    private JwtService jwtService;

    @PutMapping("/{id}")
    @ResponseBody
    public User updateUser(@RequestBody UserRequest user, @PathVariable Integer id) throws UserException, ExecutionControl.UserException {
        return userService.updateUser(user, id);
    }

    @GetMapping("/home")
    public ResponseEntity<ResponseObject> home() {
        try {
            List<Product> data = productService.getAllProducts();
            return new ResponseEntity<ResponseObject>(ResponseObject.builder().status("SUCCESS").data(data).message("List product successfully!").build(), HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<ResponseObject>(ResponseObject.builder().status("ERROR").message(exception.getMessage()).build(), HttpStatus.NOT_IMPLEMENTED);
        }
    }

    @GetMapping("/profile")
    @ResponseBody
    public ResponseEntity<ResponseObject> getDetailUser(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return new ResponseEntity<>(ResponseObject.builder().status("ERROR").message("Invalid authorization header.").build(), HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);
            ExtractUser userInfo = new ExtractUser(token, userService, jwtService);
            if (!userInfo.isEnabled()) {
                return new ResponseEntity<>(ResponseObject.builder().status("ERROR").message("User is not enabled.").build(), HttpStatus.UNAUTHORIZED);
            }

            Long userId = userInfo.getUserId();
            User user = userService.getUserById(Math.toIntExact(userId));

            Map<String, String> data = new LinkedHashMap<>();
            data.put("username", user.getUsername());
            data.put("email", user.getEmail());
            data.put("phonenumber", user.getPhoneNumber());

            return new ResponseEntity<>(ResponseObject.builder().status("SUCCESS").message("Loading data success!").data(data).build(), HttpStatus.OK);
        } catch (UserException e) {
            return new ResponseEntity<>(ResponseObject.builder().status("ERROR").message(e.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseObject.builder().status("ERROR").message("Failed to get user information.").build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
