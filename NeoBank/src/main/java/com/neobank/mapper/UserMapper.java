package com.neobank.mapper;

import com.neobank.dto.auth.LoginResponseDto;
import com.neobank.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "role", expression = "java(user.getRole().name())")
    @Mapping(target = "token", ignore = true)
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    LoginResponseDto toLoginResponse(User user);
}