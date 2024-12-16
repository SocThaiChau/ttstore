package com.example.back_end.service.impl;

import com.example.back_end.auth.JwtService;
import com.example.back_end.config.ExtractUser;
import com.example.back_end.exception.NotFoundException;
import com.example.back_end.exception.UnauthorizedException;
import com.example.back_end.exception.UserException;
import com.example.back_end.model.dto.category.CategoryDTO;
import com.example.back_end.model.entity.Category;
import com.example.back_end.model.entity.User;
import com.example.back_end.model.response.CategoryResponse;
import com.example.back_end.repository.CategoryRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    @Autowired
    private FileStorageService fileStorageService;
    private final String uploadDir = "D:/TLCN/ttstore/uploads/categories";


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

    public CategoryDTO addCategory(String name, MultipartFile imageFile, String imageUrl) throws IOException, IOException {
        String imagePath = null;

        // Xử lý nếu tải lên tệp
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = imageFile.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);

            // Tạo thư mục nếu chưa tồn tại
            if (!uploadPath.toFile().exists()) {
                uploadPath.toFile().mkdirs();
            }
            // Lưu tệp vào thư mục
            File destinationFile = new File(uploadPath.toString(), fileName);
            imageFile.transferTo(destinationFile);

            imagePath = "D:/TLCN/ttstore/uploads/categories" + fileName; // Đường dẫn tệp tải lên
        } else if (imageUrl != null && !imageUrl.isEmpty()) {
            // Nếu người dùng nhập URL
            imagePath = imageUrl;
        }

        if (imagePath == null) {
            throw new IllegalArgumentException("Vui lòng cung cấp hình ảnh hoặc URL.");
        }

        // Lưu thông tin danh mục vào database
        Category category = new Category();
        category.setName(name);
        category.setImage(imagePath);

        Category savedCategory = categoryRepository.save(category);

        // Chuyển đổi entity thành DTO để trả về
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(savedCategory.getId());
        categoryDTO.setName(savedCategory.getName());
        categoryDTO.setImage(savedCategory.getImage());

        return categoryDTO;
    }
}