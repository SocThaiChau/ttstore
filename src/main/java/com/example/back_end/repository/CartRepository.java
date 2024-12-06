package com.example.back_end.repository;

import com.example.back_end.model.entity.Cart;
import com.example.back_end.model.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Cart findByUser(User user);

    Optional<Cart> findByUserId(Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem ci WHERE ci.product.id IN :productIds AND ci.cart.user.id = :userId")
    void deleteCartItemsByProductIdsAndUserId(@Param("productIds") List<Long> productIds, @Param("userId") Long userId);

}
