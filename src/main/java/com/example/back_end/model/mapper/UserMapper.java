package com.example.back_end.model.mapper;

import com.example.back_end.model.entity.User;
import com.example.back_end.model.request.UserRequest;
import com.example.back_end.model.response.UserResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Component
@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.name", target = "name")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.phone", target = "phone")
    @Mapping(source = "user.dob", target = "dob")
    @Mapping(source = "user.role", target = "role")
    @Mapping(source = "user.password", target = "password")
    @Mapping(source = "user.gender", target = "gender")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.phoneNumber", target = "phoneNumber")
    @Mapping(source = "user.avatarUrl", target = "avatarUrl")
    @Mapping(source = "user.otp", target = "otp")
    @Mapping(source = "user.createBy", target = "createBy")
    @Mapping(source = "user.lastModyfiedBy", target = "lastModyfiedBy")
    @Mapping(source = "user.otpCreateTime", target = "otpCreateTime")
    @Mapping(source = "user.createDate", target = "createDate")
    @Mapping(source = "user.lastModifiedDate", target = "lastModifiedDate")
    UserResponse toResponse(User user);

    List<UserResponse> toUserListDTO(List<User> users);

    User toEntity(UserRequest userRequest);

}

