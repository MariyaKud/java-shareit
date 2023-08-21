package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    Long getId();

    User save(User user);

    User getById(Long id);

    User getByEmail(String email);

    User delete(Long id);

    List<User> findAll();
}
