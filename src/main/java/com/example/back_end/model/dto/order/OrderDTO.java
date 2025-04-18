package com.example.back_end.model.dto.order;

import com.example.back_end.model.entity.OrderItem;
import com.example.back_end.model.entity.OrderParent;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Hidden
public class OrderDTO {

    private Long id;

    private Long storeId;

    private String status;

    private Double total;

    private Date createdDate;

    private Date lastModifiedDate;

    private Long addressId;
}
