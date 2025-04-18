package com.example.back_end.model.request;

import com.example.back_end.model.entity.Order;
import com.example.back_end.model.entity.Product;
import com.example.back_end.model.entity.Review;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemRequest implements Serializable {

    private Long productId;
    private Integer quantity;
}
