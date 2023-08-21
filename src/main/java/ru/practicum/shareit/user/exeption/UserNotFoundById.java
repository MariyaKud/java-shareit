package ru.practicum.shareit.user.exeption;

public class UserNotFoundById extends RuntimeException {
    public UserNotFoundById(Long id) {
        super(String.format("Пользователь с id = %s не найден", id));
    }
}
