package com.example.back_end.service.impl;

import com.example.back_end.exception.UserException;
import com.example.back_end.model.entity.Cart;
import com.example.back_end.model.entity.CartItem;
import com.example.back_end.model.entity.Product;
import com.example.back_end.model.entity.User;
import com.example.back_end.repository.CartItemRepository;
import com.example.back_end.repository.CartRepository;
import com.example.back_end.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    //private final UserRepository userRepository;

    @Autowired
    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }
    public Cart getCartById(Long cartId) {
        return cartRepository.findById(cartId)
                .orElse(null);
    }

    public void addCartItem(CartItem cartItem) {
        cartItemRepository.save(cartItem);
    }
    public void updateCartItem(CartItem cartItem) {
        cartItemRepository.save(cartItem);
    }

    public CartItem findCartItemByProduct(Long cartId, Long productId) {
        return cartItemRepository.findByCartIdAndProductId(cartId, productId);
    }
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    public void saveCart(Cart cart) {
        cartRepository.save(cart);
    }

    public CartItem getCartItemByProduct(Cart cart, Product product) {
        for (CartItem item : cart.getCartItemList()) {
            if (item.getProduct().equals(product)) {
                return item;
            }
        }
        return null;
    }

    public void updateCart(Cart cart) {
        // Cập nhật thông tin giỏ hàng trong cơ sở dữ liệu hoặc lưu trữ
        // Ví dụ: Sử dụng JPA để lưu trữ thông tin giỏ hàng trong cơ sở dữ liệu

        // Cập nhật thông tin giỏ hàng
        cart.setTotalItem(calculateTotalItem(cart));
        cart.setTotalPrice(calculateTotalPrice(cart));

        // Cập nhật ngày chỉnh sửa cuối cùng
        cart.setLastModifiedDate(new Date());

        // Lưu giỏ hàng đã cập nhật vào cơ sở dữ liệu
        cartRepository.save(cart);
    }
    private int calculateTotalItem(Cart cart) {
        List<CartItem> cartItems = cart.getCartItemList();
        int totalItem = 0;
        for (CartItem cartItem : cartItems) {
            totalItem += cartItem.getQuantity();
        }
        return totalItem;
    }
    private double calculateTotalPrice(Cart cart) {
        List<CartItem> cartItems = cart.getCartItemList();
        double totalPrice = 0;
        for (CartItem cartItem : cartItems) {
            totalPrice += cartItem.getSubtotal();
        }
        return totalPrice;
    }
    public Cart getOrCreateCart(User user) {
        Cart cart = cartRepository.findByUser(user);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setCartItemList(new ArrayList<>());
            cart.setTotalItem(0); // Khởi tạo giá trị mặc định cho totalItem
            cart.setTotalPrice(0.0); // Khởi tạo giá trị mặc định cho totalPrice
            cart.setCreatedDate(new Date()); // Khởi tạo thời gian tạo giỏ hàng
            cart.setLastModifiedDate(new Date()); // Khởi tạo thời gian sửa đổi cuối cùng
            cart = cartRepository.save(cart);
        }
        return cart;
    }
}