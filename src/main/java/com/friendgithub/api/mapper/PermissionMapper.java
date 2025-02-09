package com.friendgithub.api.mapper;

import com.friendgithub.api.dto.request.PermissionRequest;
import com.friendgithub.api.dto.response.PermissionResponse;
import com.friendgithub.api.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
