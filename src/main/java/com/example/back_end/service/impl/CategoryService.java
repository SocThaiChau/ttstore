package com.example.back_end.service.impl;

import com.example.back_end.exception.NotFoundException;
import com.example.back_end.model.entity.Category;
import com.example.back_end.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository  categoryRepository;

    public Category getCategoryById(Long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if (categoryOptional.isPresent()) {
            return categoryOptional.get();
        } else {
            throw new NotFoundException("Category not found with ID: " + categoryId);
        }
    }
}