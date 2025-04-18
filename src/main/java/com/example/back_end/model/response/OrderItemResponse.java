package com.example.back_end.model.response;

import com.example.back_end.model.entity.Product;
import com.example.back_end.model.entity.Review;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class OrderItemResponse implements Serializable {
    private Long id;
    private Integer quantity;
    private Long productId;  // Only product ID
    private Double subtotal;
    private Date createdDate;
    private Date lastModifiedDate;
}
