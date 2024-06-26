package com.example.back_end.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse implements Serializable {
    private Long id;
    private String username;
    private String email;
    private String name;
    private String password;
    private String phoneNumber;
    private String gender;
    private String avatarUrl;
    private String otp;
    private UserResponse createBy;
    private String lastModyfiedBy;
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date otpCreateTime;
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastModifiedDate;
    private UserRoleResponse userRoleResponse;
    private Long userRoleId;

}
