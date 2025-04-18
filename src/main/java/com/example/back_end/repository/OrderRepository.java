package com.example.back_end.repository;

import com.example.back_end.model.entity.Order;
import com.example.back_end.model.entity.OrderParent;
import com.example.back_end.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStoreId(Long id);

//    List<Order> findByUser(User user);
}
