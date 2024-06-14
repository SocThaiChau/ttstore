package com.example.back_end.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
@Table(name = "Product")
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Double price;

    @Column(name = "promotionalPrice")
    private Double promotionalPrice;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "quantityAvailable")
    private Integer quantityAvailable;

    @Column(name = "numberOfRating")
    private Integer numberOfRating;

    @Column(name = "favoriteCount")
    private Integer favoriteCount;

    @Column(name = "sold")
    private Integer sold;

    @Column(name = "isActive")
    private Boolean isActive;

    @Column(name = "isSelling")
    private Boolean isSelling;

    @Column(name = "rating")
    private Float rating;

    @Column(name = "createdBy")
    private String createdBy;

    @Column(name = "lastModifiedBy")
    private String lastModifiedBy;

    @Column(name = "createdDate")
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDate;

    @Column(name = "lastModifiedDate")
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastModifiedDate;




    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Image> images;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Review> productReviewList;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private OrderItem orderItem;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CartItem cartItem;

    public void setActive(boolean b) {

    }

    public void setCreatedDate(Date createdDate) {
    }
}
