package com.example.back_end.repository;

import com.example.back_end.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findBydescriptionContaining(String keyword);
}
