package com.example.back_end.model.dto.user;

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
public class UserDTO {
    private Long id;

    private String username;

    private String email;

    private String name;

    private String password;

    private String phoneNumber;

    private String gender;

    private String address;

    private String avatarUrl;

    private Date dob;

    private String otp;

    private String lastModyfiedBy;

    private Date otpCreateTime;

    private Date createDate;

    private Date lastModifiedDate;

    private Boolean checkPassword;
}
