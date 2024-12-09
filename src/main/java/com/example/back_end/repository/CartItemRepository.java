package com.example.back_end.repository;

import com.example.back_end.model.entity.Cart;
import com.example.back_end.model.entity.CartItem;
import com.example.back_end.model.entity.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByCartIdAndProductId(Long cartId, Long productId);
    CartItem findByCartAndProduct(Cart cart, Product product);

    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem ci WHERE ci.product.id IN :productIds AND ci.cart.user.id = :userId")
    void deleteCartItemsByProductIdsAndUserId(@Param("productIds") List<Long> productIds, @Param("userId") Long userId);
}
