package com.example.back_end.model.request;

import lombok.Getter;

@Getter
public class AddToCartRequest {
    private Integer productId;
    private Integer quantity;

    // Thêm các getter và setter

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}