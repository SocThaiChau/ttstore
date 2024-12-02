package com.example.back_end.controller;

import com.example.back_end.model.dto.category.CategoryDTO;
import com.example.back_end.model.entity.Category;
import com.example.back_end.model.entity.Product;
import com.example.back_end.response.ResponseObject;
import com.example.back_end.service.impl.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    @GetMapping("/getAll")
    public ResponseEntity<?> getCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }


}
