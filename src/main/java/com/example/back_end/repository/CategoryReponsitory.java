package com.example.back_end.repository;

import com.example.back_end.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryReponsitory extends JpaRepository<Category, Long> {

}
