package com.example.back_end.controller;

import com.example.back_end.exception.NotFoundException;
import com.example.back_end.model.dto.category.CategoryDTO;
import com.example.back_end.model.request.CategoryRequest;
import com.example.back_end.model.response.CategoryResponse;
import com.example.back_end.repository.CategoryRepository;
import com.example.back_end.response.ResponseObject;
import com.example.back_end.service.impl.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryRepository categoryRepository;
    @GetMapping("/getAll")
    public ResponseEntity<?> getCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size) {

        Page<CategoryResponse> categoriesPage = categoryService.getAllCategories(PageRequest.of(page, size));

        // Chuẩn bị cấu trúc JSON
        Map<String, Object> response = new HashMap<>();
        response.put("content", categoriesPage.getContent());
        response.put("pageNumber", categoriesPage.getNumber());
        response.put("totalPages", categoriesPage.getTotalPages());
        response.put("totalElements", categoriesPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getCategoriesById(@PathVariable Long id) {
        CategoryDTO categories = categoryService.getCategoryByIdDTO(id);
        return ResponseEntity.ok(categories);
    }


    @PostMapping("/addCategory")
    public ResponseEntity<?> addCategory(@RequestBody CategoryRequest categoryRequest) {
        try {
            // Kiểm tra dữ liệu hợp lệ
            if (categoryRequest.getName() == null || categoryRequest.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Name is required");
            }
            if (categoryRequest.getImage() == null || categoryRequest.getImage().trim().isEmpty()) {
                throw new IllegalArgumentException("Image URL is required");
            }

            // Chuyển tiếp dữ liệu tới service để tạo category
            CategoryDTO createdCategory = categoryService.addCategory(
                    categoryRequest.getName(),
                    categoryRequest.getImage()
            );
            return ResponseEntity.ok(createdCategory);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/updateCategory/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long id,
            @RequestBody @Valid CategoryRequest categoryRequest) {
        try {
            // Gửi thông tin cập nhật đến service
            CategoryDTO updatedCategory = categoryService.updateCategory(
                    id,
                    categoryRequest.getName(),
                    categoryRequest.getImage()
            );

            // Trả về thông tin của categoryDTO trong phản hồi
            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status("success")
                            .message("Cập nhật danh mục thành công")
                            .data(updatedCategory)
                            .build()
            );
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseObject.builder()
                            .status("error")
                            .message(e.getMessage())
                            .data(null)
                            .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseObject.builder()
                            .status("error")
                            .message(e.getMessage())
                            .data(null)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .status("error")
                            .message("An unexpected error occurred")
                            .data(null)
                            .build());
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        try {
            // Lấy thông tin danh mục từ CategoryService
            CategoryDTO categoryDTO = categoryService.getCategoryByIdDTO(id);

            // Trả về phản hồi JSON chứa thông tin danh mục
            return ResponseEntity.ok(categoryDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
