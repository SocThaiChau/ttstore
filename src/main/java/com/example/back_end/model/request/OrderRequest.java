package com.example.back_end.model.request;

import com.example.back_end.model.entity.Address;
import com.example.back_end.model.entity.OrderItem;
import com.example.back_end.model.entity.Review;
import com.example.back_end.model.entity.User;
import com.example.back_end.model.response.OrderItemResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class OrderRequest implements Serializable {
    private Long idAddress;
    private String note;
    private Boolean isPaidBefore;
    private String status;
    private String paymentType;
    private List<OrderItemRequest> orderItems;
}
