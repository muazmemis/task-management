package dev.muazmemis.finalproject.mapper;

import dev.muazmemis.finalproject.dto.auth.RegisterRequest;
import dev.muazmemis.finalproject.dto.user.UserRequest;
import dev.muazmemis.finalproject.dto.user.UserResponse;
import dev.muazmemis.finalproject.dto.user.UserUpdateRequest;
import dev.muazmemis.finalproject.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "active", constant = "true")
    User toEntity(UserRequest userRequest);

    User toEntity(UserUpdateRequest userRequest);

    @Mapping(target = "active", constant = "true")
    User toEntity(RegisterRequest request);

    User toEntity(UserResponse userResponse);

    @Mapping(target = "password", constant = "***")
    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);
}
