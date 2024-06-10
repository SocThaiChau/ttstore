package com.example.back_end.repository;

import com.example.back_end.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Các phương thức tùy chọn khác nếu cần

    // Ví dụ: Tìm kiếm category bằng ID
    Optional<Category> findById(Long categoryId);
}