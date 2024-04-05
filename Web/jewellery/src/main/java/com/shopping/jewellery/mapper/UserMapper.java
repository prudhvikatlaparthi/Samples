package com.shopping.jewellery.mapper;

import com.shopping.jewellery.dto.UserDTO;
import com.shopping.jewellery.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User dtoToEntity(UserDTO userDto);

    UserDTO entityToDto(User user);
}