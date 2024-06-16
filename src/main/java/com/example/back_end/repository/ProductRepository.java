package com.example.back_end.repository;

import com.example.back_end.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findBydescriptionContaining(String keyword);
    List<Product> findByUserId(Long userId);
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword%")
    List<Product> findByKeyword(@Param("keyword") String keyword);
}
