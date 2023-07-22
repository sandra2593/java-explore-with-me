package ru.practicum.ewm.user.mapper;

import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.model.User;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return UserDto.builder().id(user.getId()).email(user.getEmail()).name(user.getName()).build();
    }

    public static User fromUserDto(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        return user;
    }

    public static User fromUserRequestDto(NewUserRequest userRequestDto) {
        User user = new User();
        user.setEmail(userRequestDto.getEmail());
        user.setName(userRequestDto.getName());
        return user;
    }

    public static UserDto toUserRequestDto(User user) {
        return UserDto.builder().email(user.getEmail()).name(user.getName()).build();
    }

    public static User fromUserShortDto(UserShortDto userShortDto) {
        User user = new User();
        user.setId(userShortDto.getId());
        user.setName(userShortDto.getName());
        return user;
    }

    public static UserShortDto toUserShortDto(User user) {
        return UserShortDto.builder().id(user.getId()).name(user.getName()).build();
    }
}
