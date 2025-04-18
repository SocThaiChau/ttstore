package com.example.back_end.model.dto.orderItem;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Hidden
public class OrderItemDTO {

    private Long id;

    private Integer quantity;

    private Double price;

    private Double subtotal;

    private String imageUrl;

    private Date createdDate;

    private Date lastModifiedDate;

    private Long storeId;

    private Long productId;
}
