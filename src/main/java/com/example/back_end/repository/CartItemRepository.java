package com.example.back_end.repository;

import com.example.back_end.model.entity.Cart;
import com.example.back_end.model.entity.CartItem;
import com.example.back_end.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByCartIdAndProductId(Long cartId, Long productId);
    CartItem findByCartAndProduct(Cart cart, Product product);
}
