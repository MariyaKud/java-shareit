package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exeption.UserWithEmailAlreadyExist;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                             .stream()
                             .map(userMapper::mapperUserToDto)
                             .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setId(null);
        checkEmail(userDto, 0L);
        final User newUser = userMapper.mapperUserFromDto(userDto);
        userRepository.save(newUser);
        return userMapper.mapperUserToDto(newUser);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        final User originUser = findUserById(userId);

        String name = userDto.getName();
        if (name != null && !name.isBlank()) {
            originUser.setName(name);
        }
        if (userDto.getEmail() != null) {
            checkEmail(userDto, userId);
            originUser.setEmail(userDto.getEmail());
        }

        final User updateUser = userRepository.save(originUser);
        return userMapper.mapperUserToDto(updateUser);
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
        return userRepository.getById(userId)
                .orElseThrow(() -> new EntityNotFoundException(userId, User.class));
    }

    private void checkEmail(UserDto userDto, Long userId) {
        Optional<User> user = userRepository.getByEmailWithAnotherId(userDto.getEmail(), userId);
        if (user.isPresent()) {
            throw new UserWithEmailAlreadyExist(userDto.getEmail());
        }
    }
}
