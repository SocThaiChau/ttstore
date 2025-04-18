package com.example.back_end.controller;

import com.example.back_end.auth.JwtService;
import com.example.back_end.config.ExtractUser;
import com.example.back_end.exception.UnauthorizedException;
import com.example.back_end.exception.UserException;
import com.example.back_end.model.entity.Cart;
import com.example.back_end.model.entity.CartItem;
import com.example.back_end.model.entity.Product;
import com.example.back_end.model.entity.User;
import com.example.back_end.model.request.AddToCartRequest;
import com.example.back_end.model.response.CartItemResponse;
import com.example.back_end.model.response.CartResponse;
import com.example.back_end.response.ResponseObject;
import com.example.back_end.service.impl.CartItemService;
import com.example.back_end.service.impl.CartService;
import com.example.back_end.service.impl.ProductService;
import com.example.back_end.service.impl.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CartItemService cartItemService;
    @Autowired
    private CartService cartService;
    @Autowired
    private JwtService jwtService;

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

    @PostMapping("/add")
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
            newCartItem.setPrice(product.getPromotionalPrice());
            newCartItem.setSubtotal(product.getPromotionalPrice() * addToCartRequest.getQuantity());
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

    @GetMapping("/cartDetail")
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
}
