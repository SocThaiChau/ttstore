package com.example.back_end.model.dto.order;

import com.example.back_end.model.dto.orderItem.OrderItemDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest implements Serializable {
    private List<OrderItemDTO> items;
    private Long addressId;
    private String paymentType;
    private Boolean isPaidBefore;
    private String status;
}
