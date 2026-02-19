package com.example.schoolgestapp.gestions_users.mapper;

import com.example.schoolgestapp.entity.User;
import com.example.schoolgestapp.gestions_users.dto.UserResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDTO toDto(User user);
}
