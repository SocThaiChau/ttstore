package com.example.back_end.model.request;

import com.example.back_end.model.entity.*;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class ProductRequest implements Serializable {

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

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDate;

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastModifiedDate;

    private User user;

    private List<Image> images;

    private List<Review> productReviewList;

    private OrderItem orderItem;

    private Category category;

    private CartItem cartItem;

}
