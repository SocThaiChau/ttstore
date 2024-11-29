package com.example.back_end.model.dto.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Hidden
public class ProductDTO {

    private Long id;

    private String name;

    private String description;

    private Double price;

    private Double promotionalPrice;

    private Integer quantity;

    private Integer quantityAvailable;

    private Integer numberOfRating;

    private Integer favoriteCount;

    private Integer sold;

    private Boolean isActive;

    private Boolean isSelling;

    private Float rating;

    private String createdBy;

    private String lastModifiedBy;

    private Date createdDate;

    private Date lastModifiedDate;

    private String url;

}
