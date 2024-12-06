package com.example.back_end.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
@Table(name = "OrderParent")
public class OrderParent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status")
    private String status;

    @Column(name = "total")
    private Double total;

    @Column(name = "isPaidBefore")
    private Boolean isPaidBefore;

    @Column(name = "paymentType")
    private String paymentType;

    @Column(name = "createdDate")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDate;

    @OneToMany(mappedBy = "orderParent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> childOrders;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
