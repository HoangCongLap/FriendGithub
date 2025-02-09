package com.friendgithub.api.mapper;

import com.friendgithub.api.dto.request.UserCreationRequest;
import com.friendgithub.api.dto.request.UserUpdateRequest;
import com.friendgithub.api.dto.response.UserResponse;
import com.friendgithub.api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
