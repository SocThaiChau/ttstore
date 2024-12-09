package com.example.back_end.service.impl;

import com.example.back_end.exception.NotFoundException;
import com.example.back_end.model.dto.category.CategoryDTO;
import com.example.back_end.model.entity.Category;
import com.example.back_end.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class CategoryService {
    ModelMapper modelMapper;
    @Autowired
    private CategoryRepository  categoryRepository;


    public List<CategoryDTO> getAllCategories() {
        try {
            List<Category> categories = categoryRepository.findAll();
            return categories.stream()
                    .map(category -> modelMapper.map(category, CategoryDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw ex;
        }
    }


    public Category getCategoryById(Long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if (categoryOptional.isPresent()) {
            return categoryOptional.get();
        } else {
            throw new NotFoundException("Category not found with ID: " + categoryId);
        }
    }

    public CategoryDTO getCategoryByIdDTO(Long categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        return mapToDTO(category.get());
    }

    private CategoryDTO mapToDTO(Category category){
        CategoryDTO dto =modelMapper.map(category, CategoryDTO.class);
        return dto;
    }
}