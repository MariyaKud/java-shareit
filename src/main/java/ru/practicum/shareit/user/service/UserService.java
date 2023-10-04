package ru.practicum.shareit.user.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers(PageRequest pageRequest);

    UserDto createUser(UserDto user);

    UserDto updateUser(Long userId, UserDto user);

    UserDto getUserById(long userId);

    Boolean deleteUserById(long userId);
}
