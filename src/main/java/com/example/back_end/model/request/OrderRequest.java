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
    private Long id;

    private String status;

    private String note;

    private Double total;

    private Boolean isPaidBefore;

    private String paymentType;

    private Integer totalItem;

    private String createdBy;

    private String lastModifiedBy;

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDate;

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastModifiedDate;

    private User user;

    private Address address;

    @JsonIgnoreProperties({"orderItemResponses"})
    private List<OrderItemResponse> orderItemResponses;

    private List<OrderItem> orderItems;
    private Long idAddress;

}
