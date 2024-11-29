package com.example.back_end.model.dto.product;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Hidden
public class UserCreateProductDTO {

    private Long id;

    private String name;

    private List<ProductDTO> productDTO;
}
