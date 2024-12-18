package com.example.back_end.service.impl;

import com.example.back_end.auth.JwtService;
import com.example.back_end.exception.NotFoundException;
import com.example.back_end.model.dto.category.CategoryDTO;
import com.example.back_end.model.entity.Category;
import com.example.back_end.model.response.CategoryResponse;
import com.example.back_end.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class CategoryService {
    ModelMapper modelMapper;
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;

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

    private CategoryDTO mapToDTO(Category category) {
        CategoryDTO dto = modelMapper.map(category, CategoryDTO.class);
        return dto;
    }

    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(category ->
                new CategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getImage()
                )
        );
    }

    public Page<CategoryDTO> getCategoriesWithPagination(Pageable pageable) {
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        return categoryPage.map(this::convertToDTO);
    }

    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setImage(category.getImage());
        return dto;
    }


    public CategoryDTO addCategory(String name, String imageUrl) {
        if (name == null || name.isEmpty() || imageUrl == null || imageUrl.isEmpty()) {
            throw new IllegalArgumentException("Name and Image URL must not be empty.");
        }

        Category category = new Category();
        category.setName(name);
        category.setImage(imageUrl);
        category.setCreateDate(new Date());
        category = categoryRepository.save(category);

        // Chuyển đổi sang DTO
        return new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getImage()
        );
    }
    public CategoryDTO updateCategory(Long id, String name, String image) {
        // Kiểm tra tồn tại danh mục
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with ID " + id + " not found"));

        // Cập nhật thông tin
        if (name != null && !name.trim().isEmpty()) {
            category.setName(name);
        }
        if (image != null && !image.trim().isEmpty()) {
            category.setImage(image);
        }
        category.setLastModifiedDate(new Date()); // Lưu thời gian cập nhật (nếu có trường này)

        // Lưu thay đổi vào cơ sở dữ liệu
        categoryRepository.save(category);

        // Chuyển đổi thực thể sang DTO để trả về
        return convertToDTO(category);
    }

}