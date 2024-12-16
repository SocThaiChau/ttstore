package com.example.back_end.controller;

import com.example.back_end.model.dto.category.CategoryDTO;
import com.example.back_end.model.response.CategoryResponse;
import com.example.back_end.service.impl.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    private final String uploadDir = "uploads/categories/";

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
    public ResponseEntity<?> addCategory(
            @RequestParam("name") String name,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "imageUrl", required = false) String imageUrl) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                saveFile(imageFile);  // Lưu tệp vào thư mục
            }
            CategoryDTO createdCategory = categoryService.addCategory(name, imageFile, imageUrl);
            return ResponseEntity.ok(createdCategory);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Lỗi khi xử lý tệp: " + e.getMessage());
        }
    }
    private void saveFile(MultipartFile imageFile) throws IOException {
        // Đảm bảo thư mục tồn tại
        File uploadDir = new File("D:/TLCN/ttstore/uploads/categories");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs(); // Tạo thư mục nếu chưa có
        }

        // Tạo tệp đích từ tên tệp
        String fileName = imageFile.getOriginalFilename();
        File destinationFile = new File(uploadDir, fileName);

        // Lưu tệp vào thư mục
        imageFile.transferTo(destinationFile);
        System.out.println("Tệp đã được lưu vào: " + destinationFile.getAbsolutePath());
    }

}
