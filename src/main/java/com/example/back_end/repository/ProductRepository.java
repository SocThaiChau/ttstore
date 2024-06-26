package com.example.back_end.repository;

import com.example.back_end.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findBydescriptionContaining(String keyword);
    List<Product> findByUserId(Long userId);
    List<Product> findTop8ByOrderBySoldDesc();
    List<Product> findTop8ByOrderByLastModifiedDateDesc();
    Optional<Product> findById(Long id);
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword%")
    List<Product> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT SUM(p.sold) FROM Product p WHERE p.user.id = :userId")
    Long getTotalProductSoldByUser(Long userId);
    @Query("SELECT p.sold FROM Product p WHERE p.user.id = :userId")
    Long getProductSoldByUser(Long userId);
    @Query("SELECT SUM(p.sold * p.price) FROM Product p WHERE p.user.id = :userId")
    Double getTotalRevenueByUser(Long userId);
    @Query("SELECT (p.sold * p.price) FROM Product p WHERE p.user.id = :userId")
    Double getRevenueByUser(Long userId);
}
