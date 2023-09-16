package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

@Component
public class UserMapper {
    public UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public UserDtoShort toShortDto(User user) {
        return new UserDtoShort(user.getId(), user.getEmail());
    }

    public User fromDto(UserDto user) {
        return new User(user.getId(), user.getName(), user.getEmail());
    }
}
