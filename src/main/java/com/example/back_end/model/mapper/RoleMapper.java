package com.example.back_end.model.mapper;

import com.example.back_end.model.entity.Role;
import com.example.back_end.model.request.UserRoleRequest;
import com.example.back_end.model.response.UserRoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(source = "userPermission.permissionId", target = "permissionId")
    @Mapping(source = "userPermission.role", target = "role")
    @Mapping(source = "userPermission.syllabus", target = "syllabus")
    @Mapping(source = "userPermission.trainingProgram", target = "trainingProgram")
    @Mapping(source = "userPermission.classRoom", target = "classRoom")
    @Mapping(source = "userPermission.learningMaterial", target = "learningMaterial")
    @Mapping(source = "userPermission.userManagement", target = "userManagement")
    UserRoleResponse toResponse(Role role);

    List<UserRoleResponse> toUserRoleListDTO(List<Role> roles);

    Role toEntity(UserRoleRequest userRoleRequest);
    List<Role> toUserRoleListEntity(List<UserRoleRequest> userRoleRequests);

}
