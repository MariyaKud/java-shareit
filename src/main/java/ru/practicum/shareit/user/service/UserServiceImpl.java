package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exeption.UserNotFoundById;
import ru.practicum.shareit.user.exeption.UserWithEmailAlreadyExist;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::mapperUserToDto).collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        checkEmail(userDto);
        User newUser = userMapper.mapperUserFromDto(userRepository.getId(), userDto);
        userRepository.save(newUser);
        return userMapper.mapperUserToDto(newUser);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        final User originUser = findUserById(userId);
        userDto.setId(userId);

        if (userDto.getEmail() != null) {
            checkEmail(userDto);
        } else {
            userDto.setEmail(originUser.getEmail());
        }
        if (userDto.getName() == null) {
            userDto.setName(originUser.getName());
        }

        final User user = userMapper.mapperUserFromDto(userId, userDto);

        userRepository.save(user);

        return userMapper.mapperUserToDto(user);
    }

    @Override
    public UserDto getUserById(long userId) {
        final User user = findUserById(userId);
        return userMapper.mapperUserToDto(user);
    }

    @Override
    public void deleteUserById(long userId) {
        userRepository.delete(userId);
    }

    private User findUserById(long userId) {
        final User user = userRepository.getById(userId);
        if (user == null) {
            throw new UserNotFoundById(userId);
        }
        return user;
    }

    private void checkEmail(UserDto userDto) {
        User user = userRepository.getByEmail(userDto.getEmail());
        if (user != null) {
            if (!Objects.equals(user.getId(), userDto.getId())) {
                throw new UserWithEmailAlreadyExist(userDto.getEmail());
            }
        }
    }
}
