package ru.practicum.ewm.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.DuplicateException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.storage.UserStorageDb;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserServiceIntf {
    private final UserStorageDb userStorage;

    @Autowired
    public UserService(UserStorageDb userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public UserDto add(NewUserRequest newUserRequest) {
        User user = UserMapper.fromUserRequestDto(newUserRequest);
        try {
            return UserMapper.toUserDto(userStorage.save(user));
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateException(String.format("есть такой email ", user.getEmail()));
        }
    }

    @Override
    public void delete(long userId) {
        if (Objects.nonNull(userStorage.findById(userId))) {
            userStorage.deleteById(userId);
        } else {
            throw new NotFoundException(String.format("нет пользователя с id ", userId));
        }
    }

    @Override
    public List<UserDto> getAll(List<Long> userIds, Pageable pageable) {
        if (Objects.nonNull(userIds)) {
            return userStorage.findAllByIdIn(userIds, pageable)
                    .stream().map(UserMapper::toUserDto).collect(Collectors.toList());
        } else {
            return userStorage.findAll(pageable).getContent()
                    .stream().map(UserMapper::toUserDto).collect(Collectors.toList());
        }

    }

    @Override
    public UserDto getUserById(long userId) {
        Optional<User> user = userStorage.findById(userId);
        if (Objects.nonNull(user)) {
            return UserMapper.toUserDto(user.get());
        } else {
            throw new NotFoundException(String.format("нет пользователя с id ", userId));
        }
    }
}
