package com.example.back_end.repository;

import com.example.back_end.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import java.util.Optional;


@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findBydescriptionContaining(String keyword);
    List<Product> findByUserId(Long userId);
    Optional<Product> findById(Long id);
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword%")
    List<Product> findByKeyword(@Param("keyword") String keyword);
    List<Product> findByUser_IdAndSoldGreaterThan(Long userId, Integer sold);
    @Query("SELECT p.name, SUM(p.sold) AS totalSales, SUM(p.price * p.sold) AS totalRevenue " +
            "FROM Product p WHERE p.id = :productId AND p.user.id = :userId GROUP BY p.name")
    List<Object[]> getSalesByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);
    @Query("SELECT p.name, SUM(p.sold) AS totalSales, SUM(p.price * p.sold) AS totalRevenue " +
            "FROM Product p WHERE p.user.id = :userId GROUP BY p.name")
    List<Object[]> getSalesByUser(@Param("userId") Long userId);

}
