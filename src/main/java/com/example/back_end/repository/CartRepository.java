package com.example.back_end.repository;

import com.example.back_end.model.entity.Cart;
import com.example.back_end.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUserId(Long userId);

    Cart findByUser(User user);
}
