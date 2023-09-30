package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import ru.practicum.shareit.exeption.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Test User service light")
class UserServiceTest {
    UserServiceImpl userService;
    UserRepository userRepository;
    UserMapper userMapper;

    User user;

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        userMapper = mock(UserMapper.class);
        userService = new UserServiceImpl(userRepository, userMapper);

        user = new User(1L, "user 1", "user1@email");
    }

    @Test
    @DisplayName("should find all users")
    void should_find_all_users() {
        final PageImpl<User> userPage = new PageImpl<>(Collections.singletonList(user));

        when(userRepository.findAll(PageRequest.ofSize(10)))
                .thenReturn(userPage);

        final List<UserDto> userDtos = userService.getAllUsers(PageRequest.ofSize(10));

        assertEquals(1, userDtos.size());
    }

    @Test
    @DisplayName("should find user by id")
    void should_find_user_by_id() {
        Optional<User> userOpt = Optional.of(user);
        UserDto userDto = new UserDto(user.getId(), user.getName(), user.getEmail());

        when(userRepository.findById(anyLong()))
                .thenReturn(userOpt);

        when(userMapper.toDto(userOpt.get()))
                .thenReturn(userDto);

        UserDto userResult = userService.getUserById(user.getId());

        Assertions.assertEquals(userResult, userDto);

        verify(userRepository, times(1))
                .findById(user.getId());

        verify(userMapper, times(1))
                .toDto(userOpt.get());
    }

    @Test
    @DisplayName("should throw exception EntityNotFoundException for get by not exist user id ")
    void should_throw_exception_if_user_not_found_by_id() {
        when(userRepository.findById(anyLong())).thenThrow(new EntityNotFoundException(anyLong(), User.class));

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> userService.getUserById(1L));

        Assertions.assertEquals(User.class.getName() + " с id = 0 не найден.", exception.getMessage());
    }

    @Test
    @DisplayName("should throw exception EntityNotFoundException for delete by not exist user id ")
    void should_throw_exception_if_delete_use_not_found_by_id() {
        when(userRepository.findById(anyLong())).thenThrow(new EntityNotFoundException(anyLong(), User.class));

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> userService.deleteUserById(1L));

        Assertions.assertEquals(User.class.getName() + " с id = 0 не найден.", exception.getMessage());
    }

    @Test
    @DisplayName("should delete user by id")
    void should_delete_user_by_id() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        userService.deleteUserById(user.getId());


        verify(userRepository, times(1))
                .findById(user.getId());

        verify(userRepository, times(1))
                .delete(user);
    }

    @Test
    @DisplayName("should create user")
    void should_create_user() {
        final UserDto userDto = new UserDto(user.getId(), user.getName(), user.getEmail());

        when(userMapper.fromDto(any()))
                .thenReturn(user);

        when(userRepository.save(any()))
                .thenReturn(user);

        when(userMapper.toDto(any()))
                .thenReturn(userDto);

        UserDto userDtoResult = userService.createUser(userDto);

        Assertions.assertEquals(userDto.getId(), userDtoResult.getId());
        Assertions.assertEquals(userDto.getName(), userDtoResult.getName());
        Assertions.assertEquals(userDto.getEmail(), userDtoResult.getEmail());

        verify(userRepository, times(1))
                .save(user);

        verify(userMapper, times(1))
                .fromDto(any());

        verify(userMapper, times(1))
                .toDto(any());
    }

    @Test
    @DisplayName("should update user")
    void should_update_user() {
        final UserDto userDto = new UserDto(user.getId(), user.getName(), user.getEmail());

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any()))
                .thenReturn(user);

        when(userMapper.toDto(any()))
                .thenReturn(userDto);

        UserDto userDtoResult = userService.updateUser(userDto.getId(), userDto);

        Assertions.assertEquals(userDto.getId(), userDtoResult.getId());
        Assertions.assertEquals(userDto.getName(), userDtoResult.getName());
        Assertions.assertEquals(userDto.getEmail(), userDtoResult.getEmail());

        verify(userRepository, times(1))
                .findById(user.getId());

        verify(userRepository, times(1))
                .save(user);

        verify(userMapper, times(1))
                .toDto(any());
    }
}