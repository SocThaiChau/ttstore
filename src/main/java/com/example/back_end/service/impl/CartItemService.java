package com.example.back_end.service.impl;

import com.example.back_end.model.entity.*;
import com.example.back_end.repository.CartItemRepository;
import com.example.back_end.repository.CartRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CartItemService {

    private final CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    public CartItemService(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }

    public CartItem findCartItemByCartAndProduct(Cart cart, Product product) {
        return cartItemRepository.findByCartAndProduct(cart, product);
    }

    @Transactional
    public String deleteCartItem(Long cartItemId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();
            Long userId = currentUser.getId();
            CartItem cartItem = cartItemRepository.findById(cartItemId)
                    .orElseThrow(() -> new RuntimeException("CartItem not found with ID: " + cartItemId));

            Cart cart = cartItem.getCart();

            if (!cart.getUser().getId().equals(userId)) {
                return "Unauthorized to delete this cart item";
            }

            cart.getCartItemList().remove(cartItem);
            cart.setTotalItem(cart.getTotalItem() - cartItem.getQuantity());
            cart.setTotalPrice(cart.getTotalPrice() - cartItem.getSubtotal());

            cartItemRepository.delete(cartItem);
            cartRepository.save(cart);

            return "Delete CartItem Successfully...";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error while deleting cart item: " + e.getMessage();
        }
    }
}