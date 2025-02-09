package com.friendgithub.api.mapper;

import com.friendgithub.api.dto.request.RoleRequest;
import com.friendgithub.api.dto.response.RoleResponse;
import com.friendgithub.api.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
