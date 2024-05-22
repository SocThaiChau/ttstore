package com.example.back_end.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
@Table(name = "Users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "password")
    private String password;

    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Column(name = "gender")
    private String gender;

    @Column(name = "address")
    private String address;

    @Column(name = "avatarUrl")
    private String avatarUrl;

    @Column(name = "dob")
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dob;

    @Column(name = "otp")
    private String otp;

    @Column(name = "lastModyfiedBy")
    private String lastModyfiedBy;

    @Column(name = "otpCreateTime")
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date otpCreateTime;

    @Column(name = "createDate")
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    @Column(name = "lastModifiedDate")
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastModifiedDate;


    @Column(name = "checkPassword")
    private Boolean checkPassword;

    @ManyToOne
    @JoinColumn(name = "user_role_id")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id")
    @JsonBackReference
    private User createdBy;


    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<User> createdUsers;

    @OneToMany(mappedBy = "addressUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Address> addressList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Order> orderList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Review> reviewList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Product> productList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Cart> cartList;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_"+role.getRoles().name())
        );
    }
    // Getter và Setter cho các thuộc tính cần chỉnh sửa thông tin cá nhân
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    @Override
    public String getUsername() {
        return getEmail();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}
