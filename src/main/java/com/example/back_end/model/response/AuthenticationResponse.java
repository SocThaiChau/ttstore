package com.example.back_end.model.response;

import com.example.back_end.model.entity.Role;
import com.example.back_end.model.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse implements Serializable {
    private String token;
    private String email;
    private Long id;
    private String name;
    private String phoneNumber;
    private String address;
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dob;
    private Role role;
    private UserResponse userResponse;

}
