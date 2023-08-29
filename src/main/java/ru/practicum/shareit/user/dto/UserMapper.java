package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

@Component
public class UserMapper {
    public UserDto mapperUserToDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public User mapperUserFromDto(UserDto user) {
        return new User(user.getId(), user.getName(), user.getEmail());
    }
}
