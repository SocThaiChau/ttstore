package com.example.back_end.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesDTO {
    private String productName;
    private long totalSales;
    private double totalRevenue;

    // Getters, setters, constructors
}