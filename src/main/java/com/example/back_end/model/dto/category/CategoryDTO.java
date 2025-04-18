package com.example.back_end.model.dto.category;

import com.example.back_end.model.entity.Category;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Hidden
public class CategoryDTO {

    private Long id;
    private String name;
    private String image; // URL của ảnh nếu sử dụng từ đường dẫn bên ngoài

}
