package com.example.back_end.repository;

import com.example.back_end.model.entity.OrderParent;
import com.example.back_end.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderParentRepository extends JpaRepository<OrderParent,Long> {
        List<OrderParent> findByUserId(Long id);

}
