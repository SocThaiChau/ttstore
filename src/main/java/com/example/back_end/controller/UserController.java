package com.example.back_end.controller;

import com.example.back_end.exception.UserException;
import com.example.back_end.model.entity.Product;
import com.example.back_end.model.entity.User;
import com.example.back_end.model.request.UserRequest;
import com.example.back_end.response.ResponseObject;
import com.example.back_end.service.impl.ProductService;
import com.example.back_end.service.impl.UserService;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @PutMapping("/{id}")
    @ResponseBody
    public User updateUser(@RequestBody UserRequest user, @PathVariable Integer id) throws UserException, ExecutionControl.UserException {
        return userService.updateUser(user, id);
    }
    @GetMapping("/home")
    public ResponseEntity<ResponseObject> home()
    {
        try{
            List<Product> data = productService.getAllProducts();
            return new ResponseEntity<ResponseObject>(ResponseObject.builder().status("SUCCESS").data(data).message("List product successfully!").build(), HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<ResponseObject>(ResponseObject.builder().status("ERROR").message(exception.getMessage()).build(),HttpStatus.NOT_IMPLEMENTED);
        }
    }

}
