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
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    @PutMapping("/profile")
    ResponseEntity<ResponseObject> updateUser(HttpServletRequest request,
                                              @RequestBody @Valid UserRequest userRequest) throws JSONException, UserException {
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

            User user = userService.updateUser(userRequest, Math.toIntExact(userInfo.getUserId()));
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("name", user.getName());
            data.put("avatar", user.getAvatarUrl());
            data.put("phoneNumber", user.getPhoneNumber());

            return ResponseEntity.ok(ResponseObject.builder().status("Success").message("Update information successfully!").data(data).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(ResponseObject.builder().status("ERROR").message(e.getMessage()).build());
        }
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
    @GetMapping("/product/{productID}")
    ResponseEntity<ResponseObject> getDetailPosition(@PathVariable("productID")Integer id){
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

}
