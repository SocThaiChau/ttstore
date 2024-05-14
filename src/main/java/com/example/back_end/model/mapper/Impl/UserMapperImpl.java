package com.example.back_end.model.mapper.Impl;

import com.example.back_end.model.entity.Role;
import com.example.back_end.model.entity.User;
import com.example.back_end.model.mapper.UserMapper;
import com.example.back_end.model.request.UserRequest;
import com.example.back_end.model.response.UserResponse;
import com.example.back_end.model.response.UserRoleResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapperImpl implements UserMapper {
    @Override
    public UserResponse toResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponse userResponse = new UserResponse();

        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setUserRoleResponse(userRoleResponse (user.getRole()));
        userResponse.setUsername(user.getUsername());
        userResponse.setPassword(user.getPassword());
        userResponse.setUserRoleId( userRoleId ( user ) );
        userResponse.setPhoneNumber(user.getPhoneNumber());
        userResponse.setGender(user.getGender());
        userResponse.setAvatarUrl(user.getAvatarUrl());
        userResponse.setOtp(user.getOtp());
//        userResponse.setCreateBy( user.getCreatedBy() ));
        userResponse.setLastModyfiedBy(user.getLastModyfiedBy());
        userResponse.setOtpCreateTime(user.getOtpCreateTime());
        userResponse.setCreateDate(user.getCreateDate());
        userResponse.setLastModifiedDate(user.getLastModifiedDate());


        return userResponse;
    }

    @Override
    public List<UserResponse> toUserListDTO(List<User> users) {
        if ( users == null ) {
            return null;
        }

        List<UserResponse> list = new ArrayList<UserResponse>( users.size() );
        for ( User user : users ) {
            list.add( toResponse( user ) );
        }

        return list;
    }

    @Override
    public User toEntity(UserRequest userRequest) {
        if ( userRequest == null ) {
            return null;
        }

        User user = new User();

        user.setId(userRequest.getId());
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setUsername(userRequest.getUsername());
        user.setPassword(userRequest.getPassword());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setGender(userRequest.getGender());
        user.setAvatarUrl(userRequest.getAvatarUrl());
        user.setLastModyfiedBy(userRequest.getLastModyfiedBy());
        user.setOtpCreateTime(userRequest.getOtpCreateTime());
        user.setCreateDate(userRequest.getCreateDate());
        user.setLastModifiedDate(userRequest.getLastModifiedDate());


        return user;
    }

    protected UserRoleResponse userRoleResponse(Role role) {
        if ( role == null ) {
            return null;
        }

        UserRoleResponse userRoleResponse = new UserRoleResponse();

        userRoleResponse.setRoleId( role.getId() );
        if ( role.getRoles() != null ) {
            userRoleResponse.setRole( role.getRoles().name() );
        }

        return userRoleResponse;
    }

    private Long userRoleId(User user) {
        if ( user == null ) {
            return null;
        }
        Role userPermission = user.getRole();
        if ( userPermission == null ) {
            return null;
        }
        Long roleId = userPermission.getId();
        return roleId;
    }

}
