package ru.practicum.ewm.user.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.NewUserRequest;

import java.util.List;

public interface UserServiceIntf {
    UserDto add(NewUserRequest newUserRequest);

    void delete(long userId);

    List<UserDto> getAll(List<Long> userIds, Pageable pageable);

    UserDto getUserById(long userId);
}
