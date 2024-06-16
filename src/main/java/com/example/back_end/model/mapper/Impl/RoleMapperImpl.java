package com.example.back_end.model.mapper.Impl;

import com.example.back_end.model.entity.Role;
import com.example.back_end.model.entity.Roles;
import com.example.back_end.model.mapper.RoleMapper;
import com.example.back_end.model.request.UserRoleRequest;
import com.example.back_end.model.response.UserRoleResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoleMapperImpl implements RoleMapper {
    @Override
    public UserRoleResponse toResponse(Role role) {
        if ( role == null ) {
            return null;
        }

        UserRoleResponse userRoleResponse = new UserRoleResponse();

        userRoleResponse.setRoleId( role.getId() );
        if ( role.getRoles() != null ) {
            userRoleResponse.setRoles( role.getRoles().name() );
        }

        return userRoleResponse;
    }

    @Override
    public List<UserRoleResponse> toUserRoleListDTO(List<Role> roles) {
        return null;
    }

    @Override
    public Role toEntity(UserRoleRequest userRoleRequest) {
        if ( userRoleRequest == null ) {
            return null;
        }

        Role role = new Role();

        role.setId( userRoleRequest.getRoleId() );
        if ( userRoleRequest.getRole() != null ) {
            role.setRoles( Enum.valueOf( Roles.class, userRoleRequest.getRole() ) );
        }
        return role;

    }

    @Override
    public List<Role> toUserRoleListEntity(List<UserRoleRequest> userRoleRequests) {
        return null;
    }
}
