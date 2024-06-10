package com.example.back_end.controller;

import com.example.back_end.auth.JwtService;
import com.example.back_end.config.ExtractUser;
import com.example.back_end.exception.UserException;
import com.example.back_end.model.entity.*;
import com.example.back_end.model.request.AddToCartRequest;
import com.example.back_end.model.request.UserRequest;
import com.example.back_end.repository.CartRepository;
import com.example.back_end.repository.CategoryRepository;
import com.example.back_end.response.ResponseObject;
import com.example.back_end.service.impl.CartService;
import com.example.back_end.service.impl.ProductService;
import com.example.back_end.service.impl.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jdk.jshell.spi.ExecutionControl;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CartService cartService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CategoryRepository categoryRepository;

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
            data.put("avartarUrl",user.getAvatarUrl());
            data.put("gender", user.getGender());

            return ResponseEntity.ok(ResponseObject.builder().status("Success").message("Update information successfully!").data(data).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(ResponseObject.builder().status("ERROR").message(e.getMessage()).build());
        }
    }


    @GetMapping("/home")
    public ResponseEntity<ResponseObject> home() {
        try {
            List<Product> products = productService.getAllProducts();
            List<Map<String, Object>> productData = getProductData(products);

            return ResponseEntity.ok().body(ResponseObject.builder().status("SUCCESS").data(productData).message("Danh sách sản phẩm thành công!").build());
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(ResponseObject.builder().status("ERROR").message(exception.getMessage()).build());
        }
    }

    private List<Map<String, Object>> getProductData(List<Product> products) {
        List<Map<String, Object>> productData = new ArrayList<>();

        for (Product product : products) {
            Map<String, Object> productInfo = new HashMap<>();
            productInfo.put("name", product.getName());
            productInfo.put("description", product.getDescription());
            productInfo.put("price", product.getPrice());
            productInfo.put("promotionalPrice", product.getPromotionalPrice());
            productInfo.put("sold", product.getSold());
            productInfo.put("rating", product.getRating());

            productData.add(productInfo);
        }

        return productData;
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
            data.put("name", user.getName());
            data.put("email", user.getEmail());
            data.put("phonenumber", user.getPhoneNumber());
            data.put("avartarUrl",user.getAvatarUrl());
            data.put("gender", user.getGender());

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
    @GetMapping("/products")
    public ResponseEntity<ResponseObject> searchProducts(@RequestParam("keyword") String keyword) {
        try {
            List<Product> products = productService.searchProducts(keyword);
            return ResponseEntity.ok(ResponseObject.builder().status("Success").message("Search products successfully!").data(products).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder().status("ERROR").message(e.getMessage()).build());
        }
    }
    @GetMapping("/categories/{categoryId}/products")
    public ResponseEntity<ResponseObject> getProductsByCategory(@PathVariable("categoryId") Long categoryId) {
        try {
            // Tìm kiếm danh mục dựa trên ID
            Category category = categoryRepository.findById(categoryId).orElse(null);
            if (category == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder().status("ERROR").message("Category not found.").build());
            }

            // Lấy danh sách sản phẩm thuộc danh mục
            List<Product> products = category.getProductList();
            List<Map<String, Object>> productData = getProductData(products);

            return ResponseEntity.ok().body(ResponseObject.builder().status("SUCCESS").data(productData).message("Danh sách sản phẩm theo danh mục thành công!").build());
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder().status("ERROR").message(exception.getMessage()).build());
        }
    }


}
