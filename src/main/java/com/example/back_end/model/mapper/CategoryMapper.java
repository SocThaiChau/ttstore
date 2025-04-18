package com.example.back_end.model.mapper;

import com.example.back_end.model.dto.category.CategoryDTO;
import com.example.back_end.model.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDTO toCategoryDTO(Category category);
}