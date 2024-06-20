package com.example.back_end.model.response;

import com.example.back_end.model.entity.Address;
import com.example.back_end.model.entity.OrderItem;
import com.example.back_end.model.entity.Review;
import com.example.back_end.model.entity.User;
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
public class OrderResponse implements Serializable {

    private Long id;
    private String status;
    private String note;
    private Double total;
    private Boolean isPaidBefore;
    private String paymentType;
    private Integer totalItem;
    private String createdBy;
    private String lastModifiedBy;
    private Date createdDate;
    private Date lastModifiedDate;
    private Long addressId;  // Only address ID
    private Long userId;  // Only user ID
    private List<OrderItemResponse> orderItemResponses;
}
