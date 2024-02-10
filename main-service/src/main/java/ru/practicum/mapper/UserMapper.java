package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.UserDto.NewUserRequest;
import ru.practicum.dto.UserDto.UserDto;
import ru.practicum.model.User;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    public abstract UserDto entityToDto(User user);

    @Mapping(target = "id", ignore = true)

    public abstract User newToEntity(NewUserRequest newUserRequest);

}
