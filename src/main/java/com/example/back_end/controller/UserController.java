package com.example.back_end.controller;

import com.example.back_end.auth.JwtService;
import com.example.back_end.config.ExtractUser;
import com.example.back_end.exception.UnauthorizedException;
import com.example.back_end.exception.UserException;
import com.example.back_end.model.entity.*;
import com.example.back_end.model.mapper.UserMapper;
import com.example.back_end.model.request.AddToCartRequest;
import com.example.back_end.model.request.UserRequest;
import com.example.back_end.model.response.CartItemResponse;
import com.example.back_end.model.response.CartResponse;
import com.example.back_end.model.response.ProductResponse;
import com.example.back_end.repository.CartRepository;
import com.example.back_end.repository.CategoryRepository;
import com.example.back_end.repository.ImageRepository;
import com.example.back_end.response.ResponseObject;
import com.example.back_end.service.impl.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
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
    private NotificationService notificationService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryRepository categoryRepository;
    private final UserMapper userMapper;


    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private ImageRepository imageRepository;

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

    @PutMapping("/profile")
    ResponseEntity<ResponseObject> updateUser(HttpServletRequest request,
                                              @RequestBody @Valid UserRequest userRequest) throws JSONException, UserException {
        try {
            User user = authenticateUser(request);
            User updatedUser = userService.updateUser(userRequest, Math.toIntExact(user.getId()));
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("name", updatedUser.getName());
            data.put("phoneNumber", updatedUser.getPhoneNumber());
            if(updatedUser.getAvatarUrl() != null){
                data.put("avartarUrl", updatedUser.getAvatarUrl());
            }
            data.put("gender", updatedUser.getGender());
            data.put("address", updatedUser.getAddress());
            data.put("dob", updatedUser.getDob());

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
    @GetMapping("/categories")
    public ResponseEntity<ResponseObject> getCategories() {
        try {
            List<Category> categories = categoryService.getAllCategories();
            List<Map<String, Object>> categoryData = getCategoryData(categories);

            return ResponseEntity.ok().body(ResponseObject.builder()
                    .status("SUCCESS")
                    .data(categoryData)
                    .message("Categories retrieved successfully!")
                    .build());
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ResponseObject.builder()
                            .status("ERROR")
                            .message(exception.getMessage())
                            .build());
        }
    }

    private List<Map<String, Object>> getCategoryData(List<Category> categories) {
        List<Map<String, Object>> categoryData = new ArrayList<>();

        for (Category category : categories) {
            Map<String, Object> categoryInfo = new HashMap<>();
            categoryInfo.put("id", category.getId());
            categoryInfo.put("name", category.getName());
            categoryInfo.put("imageUrl",category.getImage());

            categoryData.add(categoryInfo);
        }

        return categoryData;
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

    @PostMapping("/favorite/{productId}")
    public ResponseEntity<ResponseObject> addToFavorites(HttpServletRequest request, @PathVariable("productId") Long productId) {
        try {
            User user = authenticateUser(request);
            // Get the product
            Product product = productService.getSelectedProduct(Math.toIntExact(productId));
            if (product == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder().status("ERROR").message("Product not found.").build());
            }

            // Check if the product is already in the user's favorites
            List<Product> favoriteProducts = user.getFavoriteProducts();
            if (favoriteProducts.contains(product)) {
                return ResponseEntity.ok(ResponseObject.builder().status("ERROR").message("Product already in favorites.").build());
            }

            // Add the product to the user's favorites
            favoriteProducts.add(product);
            user.setFavoriteProducts(favoriteProducts);
            userService.saveUser(user);

            // Increment the favorite count of the product
            if (product.getFavoriteCount() == null) {
                product.setFavoriteCount(0);
            }
            product.setFavoriteCount(product.getFavoriteCount() + 1);
            productService.updateProduct(product);

            // Notify other users about the new like
            notifyUsersAboutNewFavorite(product, user);

            return ResponseEntity.ok(ResponseObject.builder().status("SUCCESS").message("Product added to favorites successfully!").build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder().status("ERROR").message(e.getMessage()).build());
        }
    }

    private void notifyUsersAboutNewFavorite(Product product, User likedByUser) {
        List<User> usersToNotify = userService.getAllUsers().stream()
                .filter(u -> !u.equals(likedByUser))
                .collect(Collectors.toList());

        for (User user : usersToNotify) {
            Notification notification = Notification.builder()
                    .title("New favorite on a Product")
                    .message(likedByUser.getUsername() + " liked the product: " + product.getName())
                    .recipient(user)
                    .sender(likedByUser)
                    .createdAt(new Date())
                    .build();

            notificationService.saveNotification(notification);
        }
    }
    @DeleteMapping("/favorite/{productId}")
    public ResponseEntity<ResponseObject> removeFromFavorites(HttpServletRequest request, @PathVariable("productId") Long productId) {
        try {
            User user = authenticateUser(request);
            // Kiểm tra xem sản phẩm có tồn tại trong danh sách yêu thích hay không
            Product product = productService.getSelectedProduct(Math.toIntExact(productId));
            if (product == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder().status("ERROR").message("Product not found.").build());
            }

            List<Product> favoriteProducts = user.getFavoriteProducts();
            if (!favoriteProducts.contains(product)) {
                return ResponseEntity.ok(ResponseObject.builder().status("ERROR").message("Product not found in favorites.").build());
            }

            // Xóa sản phẩm khỏi danh sách yêu thích của người dùng
            favoriteProducts.remove(product);
            user.setFavoriteProducts(favoriteProducts);
            userService.saveUser(user);

            product.setFavoriteCount(product.getFavoriteCount() - 1);
            productService.updateProduct(product);


            return ResponseEntity.ok(ResponseObject.builder().status("SUCCESS").message("Product removed from favorites successfully!").build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder().status("ERROR").message(e.getMessage()).build());
        }
    }
    @GetMapping("/favorite")
    public ResponseEntity<ResponseObject> getFavoriteProducts(HttpServletRequest request) {
        try {
            User user = authenticateUser(request);
            // Lấy danh sách sản phẩm yêu thích của người dùng
            List<Product> favoriteProducts = user.getFavoriteProducts();

            return ResponseEntity.ok(ResponseObject.builder().status("SUCCESS").data(favoriteProducts).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder().status("ERROR").message(e.getMessage()).build());
        }
    }
    @PutMapping("/follow/{followedUserId}")
    public ResponseEntity<ResponseObject> followUser(HttpServletRequest request,
                                                     @PathVariable("followedUserId") Long followedUserId) {
        try {
            User user = authenticateUser(request);
            User followedUser = userService.getUserById(Math.toIntExact(followedUserId));
            if (followedUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseObject.builder().status("ERROR").message("Followed user not found.").build());
            }

            List<User> followedUsers = user.getFollowedUsers();
            if (followedUsers == null) {
                followedUsers = new ArrayList<>();
            }
            followedUsers.add(followedUser);
            user.setFollowedUsers(followedUsers);
            userService.saveUser(user);

            // Send notification to the followed user
            Notification notification = Notification.builder()
                    .title("New Follower")
                    .message(user.getUsername() + " has started following you.")
                    .recipient(followedUser)
                    .createdAt(new Date())
                    .build();
            notificationService.saveNotification(notification);

            return ResponseEntity.ok(ResponseObject.builder().status("SUCCESS")
                    .message("User successfully followed.").build());
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder().status("ERROR").message(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder().status("ERROR").message("Failed to follow user.").build());
        }
    }
    @DeleteMapping("/unfollow/{userId}")
    public ResponseEntity<ResponseObject> unfollowUser(HttpServletRequest request, @PathVariable("userId") Long userId) {
        try {
            User currentUser = authenticateUser(request);
            User followedUser = userService.getUserById(Math.toIntExact(userId));
            if (followedUser == null) {
                return new ResponseEntity<>(ResponseObject.builder().status("ERROR").message("Followed user not found.").build(), HttpStatus.NOT_FOUND);
            }

            List<User> followedUsers = currentUser.getFollowedUsers();
            if (!followedUsers.contains(followedUser)) {
                return new ResponseEntity<>(ResponseObject.builder().status("ERROR").message("User is not being followed.").build(), HttpStatus.BAD_REQUEST);
            }

            followedUsers.remove(followedUser);
            currentUser.setFollowedUsers(followedUsers);
            userService.saveUser(currentUser);

            return ResponseEntity.ok(ResponseObject.builder().status("SUCCESS").message("Unfollow user successfully!").build());
        } catch (UserException e) {
            return new ResponseEntity<>(ResponseObject.builder().status("ERROR").message(e.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseObject.builder().status("ERROR").message("Failed to unfollow user.").build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/followed-users")
    public ResponseEntity<ResponseObject> getFollowedUsers(HttpServletRequest request) {
        try {
            User currentUser = authenticateUser(request);
            List<User> followedUsers = currentUser.getFollowedUsers();

            List<Map<String, String>> data = new ArrayList<>();
            for (User followedUser : followedUsers) {
                Map<String, String> userData = new LinkedHashMap<>();
                userData.put("id", followedUser.getId().toString());
                userData.put("name", followedUser.getName());
                userData.put("email", followedUser.getEmail());


                data.add(userData);
            }

            return ResponseEntity.ok(ResponseObject.builder().status("SUCCESS").message("Get followed users successfully!").data(data).build());
        } catch (UserException e) {
            return new ResponseEntity<>(ResponseObject.builder().status("ERROR").message(e.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseObject.builder().status("ERROR").message("Failed to get followed users.").build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/cart/add")
    public ResponseEntity<ResponseObject> addToCart(HttpServletRequest request, @RequestBody @Valid AddToCartRequest addToCartRequest) throws UserException {
        try {
            // Xác thực người dùng
            User user = authenticateUser(request);

            System.out.println("đã vào cart");
            // Lấy hoặc tạo giỏ hàng cho người dùng
            Cart cart = cartService.getOrCreateCart(user);
            System.out.println("proId: " + addToCartRequest.getProductId());
            System.out.println("quantity: " + addToCartRequest.getQuantity());

            // Kiểm tra xem sản phẩm đã có trong giỏ hàng hay chưa
            List<CartItem> cartItems = cart.getCartItemList();
            for (CartItem cartItem : cartItems) {
                if (cartItem.getProduct().getId().equals(addToCartRequest.getProductId())) {
                    // Sản phẩm đã có trong giỏ hàng, cập nhật số lượng và tổng giá trị
                    cartItem.setQuantity(cartItem.getQuantity() + addToCartRequest.getQuantity());
                    cartItem.setSubtotal(cartItem.getSubtotal() + (addToCartRequest.getQuantity() * cartItem.getPrice()));
                    System.out.println("sản phẩm đã có trong giỏ hàng");
                    cartService.updateCart(cart);
                    return ResponseEntity.ok().body(ResponseObject.builder().status("SUCCESS").message("Product quantity updated in cart.").build());
                }
            }

            // Sản phẩm chưa có trong giỏ hàng, thêm mới vào giỏ hàng
            Product product = productService.getProductById(Long.valueOf(addToCartRequest.getProductId()));
            if (product == null) {
                return ResponseEntity.badRequest().body(ResponseObject.builder().status("ERROR").message("Product not found.").build());
            }
            CartItem newCartItem = new CartItem();
            newCartItem.setProduct(product);
            newCartItem.setQuantity(addToCartRequest.getQuantity());
            newCartItem.setPrice(product.getPrice());
            newCartItem.setSubtotal(product.getPrice() * addToCartRequest.getQuantity());
            newCartItem.setImageUrl(product.getUrl());
            newCartItem.setCart(cart);

            cartItems.add(newCartItem);
            cartService.updateCart(cart);

            return ResponseEntity.ok().body(ResponseObject.builder().status("SUCCESS").message("Product added to cart.").build());
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseObject.builder().status("ERROR").message(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder().status("ERROR").message("Failed to add product to cart.").build());
        }
    }

    @GetMapping("/cart/detail")
    public ResponseEntity<ResponseObject> getCartDetail(HttpServletRequest request) {
        try {
            // Xác thực người dùng
            User user = authenticateUser(request);

            // Lấy giỏ hàng của người dùng
            Cart cart = cartService.getOrCreateCart(user);

            // Chuyển đổi danh sách CartItem thành CartItemResponse
            List<CartItemResponse> cartItemResponses = cart.getCartItemList().stream()
                    .map(cartItem -> new CartItemResponse(
                            cartItem.getId(),
                            cartItem.getProduct().getId(),
                            cartItem.getProduct().getName(),
                            cartItem.getQuantity(),
                            cartItem.getPrice(),
                            cartItem.getSubtotal(),
                            cartItem.getImageUrl()
                    ))
                    .collect(Collectors.toList());

            // Tạo CartResponse
            CartResponse cartResponse = new CartResponse(
                    cart.getId(),
                    cart.getTotalItem(),
                    cart.getTotalPrice(),
                    cart.getCreatedDate(),
                    cart.getLastModifiedDate(),
                    cartItemResponses
            );

            ResponseObject response = ResponseObject.builder()
                    .status("SUCCESS")
                    .data(cartResponse)
                    .build();

            return ResponseEntity.ok(response);
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseObject.builder().status("ERROR").message(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder().status("ERROR").message("Failed to get cart items.").build());
        }
    }
    @PutMapping("/updatePassword")
    public ResponseEntity<?> updatePassword(@RequestBody UserRequest userRequest){
        return ResponseEntity.ok(userService.updatePassword(userRequest));
    }

    @GetMapping("/checkPassword")
    public ResponseEntity<?> checkPassword(){
        return ResponseEntity.ok(userService.checkPassword());
    }

    @GetMapping("/getUser/{userId}")
    public ResponseEntity<?> getById(@PathVariable Long userId){
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @DeleteMapping("/delete/cartItem/{id}")
    public ResponseEntity<String> deleteCartItem(@PathVariable Long id){
        String result = cartItemService.deleteCartItem(id);
        if (result.equals("Delete CartItem Successfully...")) {
            return ResponseEntity.ok(result);
        } else if (result.equals("Unauthorized to delete this address")) {
            return ResponseEntity.status(403).body(result);
        } else {
            return ResponseEntity.status(500).body(result);
        }
    }

//    @GetMapping("/categories")
//    public ResponseEntity<ResponseObject> getCategories() {
//        try {
//            List<Category> categories = categoryService.getAllCategories();
//            List<Map<String, Object>> categoryData = getCategoryData(categories);
//
//            return ResponseEntity.ok().body(ResponseObject.builder()
//                    .status("SUCCESS")
//                    .data(categoryData)
//                    .message("Categories retrieved successfully!")
//                    .build());
//        } catch (Exception exception) {
//            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
//                    .body(ResponseObject.builder()
//                            .status("ERROR")
//                            .message(exception.getMessage())
//                            .build());
//        }
//    }
//    private List<Map<String, Object>> getCategoryData(List<Category> categories) {
//        List<Map<String, Object>> categoryData = new ArrayList<>();
//
//        for (Category category : categories) {
//            Map<String, Object> categoryInfo = new HashMap<>();
//            categoryInfo.put("id", category.getId());
//            categoryInfo.put("name", category.getName());
//            categoryInfo.put("imageUrl",category.getImage());
//
//            categoryData.add(categoryInfo);
//        }
//
//        return categoryData;
//    }
}
