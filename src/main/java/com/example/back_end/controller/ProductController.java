package com.example.back_end.controller;

import com.example.back_end.auth.JwtService;
import com.example.back_end.config.ExtractUser;
import com.example.back_end.exception.UnauthorizedException;
import com.example.back_end.exception.UserException;
import com.example.back_end.model.dto.product.ProductDTO;
import com.example.back_end.model.entity.Category;
import com.example.back_end.model.entity.Product;
import com.example.back_end.model.entity.User;
import com.example.back_end.model.mapper.UserMapper;
import com.example.back_end.model.request.ProductRequest;
import com.example.back_end.model.response.ProductResponse;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vp")
public class ProductController {
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private CategoryService categoryService;

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

    @GetMapping("/getAllProduct")
    public ResponseEntity<ResponseObject> getAllProducts() {
        try {
            // Assume productService retrieves all products and returns List<ProductResponse>
            List<ProductResponse> products = productService.getAllProductsResponse();

            // Construct success response
            ResponseObject response = ResponseObject.builder()
                    .status("Success")
                    .data(products)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Handle exception and return error response
            ResponseObject errorResponse = ResponseObject.builder()
                    .status("Error")
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{categoryId}/products")
    public ResponseEntity<?> getProductsByCategory(@PathVariable("categoryId") Long categoryId) {
        List<ProductDTO> productDTOS = productService.getProductsByCategoryId(categoryId);
        return ResponseEntity.ok(productDTOS);
    }
    @GetMapping("/getProductBySold")
    public ResponseEntity<ResponseObject> getProductBySold() {
        try {
            // Assume productService retrieves all products and returns List<ProductResponse>
            List<ProductResponse> products = productService.findTop8ByOrderBySoldDesc();

            // Construct success response
            ResponseObject response = ResponseObject.builder()
                    .status("Success")
                    .data(products)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Handle exception and return error response
            ResponseObject errorResponse = ResponseObject.builder()
                    .status("Error")
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/getProductByDate")
    public ResponseEntity<ResponseObject> getProductByDate() {
        try {
            // Assume productService retrieves all products and returns List<ProductResponse>
            List<ProductResponse> products = productService.findTop8ByOrderByLastModifiedDateDesc();

            // Construct success response
            ResponseObject response = ResponseObject.builder()
                    .status("Success")
                    .data(products)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Handle exception and return error response
            ResponseObject errorResponse = ResponseObject.builder()
                    .status("Error")
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/profile")
    @ResponseBody
    public ResponseEntity<ResponseObject> getDetailUser(HttpServletRequest request) {
        try {
            User user = authenticateUser(request);

            Map<String, String> data = new LinkedHashMap<>();
            data.put("name", user.getName());
            data.put("email", user.getEmail());
            data.put("phonenumber", user.getPhoneNumber());
            data.put("avartarUrl", user.getAvatarUrl());
            data.put("gender", user.getGender());

            return ResponseEntity.ok(ResponseObject.builder().status("SUCCESS").message("Loading data success!").data(data).build());
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseObject.builder().status("ERROR").message(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder().status("ERROR").message("Failed to get user information.").build());
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

    @GetMapping("/product/detail/{productId}")
    public ResponseEntity<ResponseObject> getProductById(@PathVariable Long productId) {
        try {
            ProductResponse product = productService.getProductByIdDetail(productId);

            if (product != null) {
                ResponseObject response = ResponseObject.builder()
                        .status("Success")
                        .data(product)
                        .build();
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder()
                        .status("Error")
                        .message("Product not found with ID: " + productId)
                        .build());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                    .status("Error")
                    .message(e.getMessage())
                    .build());
        }
    }

    @GetMapping("/products")
    private List<Map<String, Object>> getProductData(List<Product> products) {
        List<Map<String, Object>> productData = new ArrayList<>();

        for (Product product : products) {
            Map<String, Object> productInfo = new HashMap<>();
            productInfo.put("id", product.getId());
            productInfo.put("name", product.getName());
            productInfo.put("description", product.getDescription());
            productInfo.put("price", product.getPrice());
            productInfo.put("promotionalPrice", product.getPromotionalPrice());
            productInfo.put("sold", product.getSold());
            productInfo.put("rating", product.getRating());
            productInfo.put("url", product.getUrl());

            productData.add(productInfo);
        }

        return productData;
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseObject> searchProducts(@RequestParam("keyword") String keyword) {
        try {
            List<Product> products = productService.searchProducts(keyword);
            List<Map<String, Object>> productData = getProductData(products);

            return ResponseEntity.ok().body(ResponseObject.builder().status("SUCCESS").data(productData).message("Search results").build());
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(ResponseObject.builder().status("ERROR").message(exception.getMessage()).build());
        }
    }

    @PostMapping("/product/create")
    public ResponseEntity<ResponseObject> createProduct(@RequestBody @Valid ProductRequest productRequest, HttpServletRequest request) throws UserException {
        try {
            User user = authenticateUser(request);
            System.out.println("đã vào thêm sản phẩm");
            // Chuyển đổi ProductRequest sang Product
            Product product = convertToProduct(productRequest, user);

            Long categoryId = productRequest.getCategoryId();
            Category category = categoryService.getCategoryById(categoryId); // Lấy thông tin Category dựa trên categoryId
            product.setCategory(category);

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
    private Product convertToProduct(ProductRequest productRequest, User user) {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setPromotionalPrice(productRequest.getPromotionalPrice());
        product.setQuantity(productRequest.getQuantity());
        product.setQuantityAvailable(productRequest.getQuantityAvailable());
        product.setNumberOfRating(1);
        product.setFavoriteCount(0);
        product.setSold(0);
        product.setIsActive(true);
        product.setIsSelling(true);
        product.setRating(1.0f);
        product.setCreatedBy(user.getName());
        product.setLastModifiedBy(user.getName());
        product.setCreatedDate(new Date());
        product.setLastModifiedDate(new Date());
        product.setUser(user);
        product.setUrl(productRequest.getUrl());
        return product;
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
