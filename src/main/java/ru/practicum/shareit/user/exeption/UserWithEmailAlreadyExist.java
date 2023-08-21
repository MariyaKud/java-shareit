package ru.practicum.shareit.user.exeption;

public class UserWithEmailAlreadyExist extends RuntimeException {
    public UserWithEmailAlreadyExist(String message) {
        super(message);
    }
}