package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("User service")
class UserServiceTest {
    UserServiceImpl userService;
    UserRepository userRepository;
    UserMapper userMapper;

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        userMapper = mock(UserMapper.class);
        userService = new UserServiceImpl(userRepository, userMapper);
    }

    @Test
    @DisplayName("should find all users")
    void should_find_all_users() {
        User user = new User(1L, "user 1", "user1@email");
        final PageImpl<User> userPage = new PageImpl<>(Collections.singletonList(user));
        when(userRepository.findAll(PageRequest.ofSize(10)))
                .thenReturn(userPage);

        final List<UserDto> userDtos = userService.getAllUsers(PageRequest.ofSize(10));

        assertEquals(1, userDtos.size());

    }
}